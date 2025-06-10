package com.bomberman.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Classe principale du jeu Bomberman.
 * Gère la grille, les joueurs (humains et IA), les bombes, les explosions et les bonus.
 */
public class Game {
    private Grid grid;
    private List<Player> players;
    private boolean gameOver;
    private Player winner;
    private List<Bomb> bombs = new ArrayList<>();
    private List<Bonus> bonuses = new ArrayList<>();
    private List<Explosion> explosions = new ArrayList<>();
    private final AIDifficulty aiDifficulty; // Ajouté

    // --- TYPES EXPLOSION POUR AFFICHAGE ---
    public enum ExplosionPartType { CENTRE, BRANCH, END }
    public enum Direction { UP, DOWN, LEFT, RIGHT }
    public static class ExplosionCell {
        public final ExplosionPartType type;
        public final Direction direction; // null pour centre
        public ExplosionCell(ExplosionPartType type, Direction direction) {
            this.type = type;
            this.direction = direction;
        }
    }
    // ---------------------------------------

    /** Permet de mémoriser précisément les centres d'explosion actifs */
    private final List<ExplosionCenter> explosionCenters = new ArrayList<>();
    private static class ExplosionCenter {
        int x, y;
        int frame;
        ExplosionCenter(int x, int y, int frame) {
            this.x = x; this.y = y; this.frame = frame;
        }
    }
    private int explosionFrameCounter = 0;

    private static class Explosion {
        int x, y;
        int ticksRemaining;
        Explosion(int x, int y, int ticksRemaining) {
            this.x = x;
            this.y = y;
            this.ticksRemaining = ticksRemaining;
        }
    }

    public Game(int width, int height, int playerCount, int iaCount, Level level, AIDifficulty aiDifficulty) {
        this.grid = new Grid(width, height, level);
        this.players = new ArrayList<>();
        this.gameOver = false;
        this.winner = null;
        this.aiDifficulty = aiDifficulty;
        initializePlayers(playerCount, iaCount);

        // Exemple de bonus fixe pour test, à adapter selon besoins
        bonuses.add(new FlameBonus(5, 3, 1));
    }

    private void initializePlayers(int humanCount, int iaCount) {
        int[][] startPositions = {
                {1, 1},
                {grid.getWidth() - 2, grid.getHeight() - 2},
                {1, grid.getHeight() - 2},
                {grid.getWidth() - 2, 1}
        };
        int totalPlayers = humanCount + iaCount;
        int index = 0;

        // Humains
        for (int i = 0; i < humanCount && index < startPositions.length; i++, index++) {
            int x = startPositions[index][0];
            int y = startPositions[index][1];
            clearSpawnZoneOnly(x, y);
            Player player = new Player(index + 1, x, y, true);
            players.add(player);
        }

        // IA
        for (int i = 0; i < iaCount && index < startPositions.length; i++, index++) {
            int x = startPositions[index][0];
            int y = startPositions[index][1];
            clearSpawnZoneOnly(x, y);
            PlayerAI ia = new PlayerAI(index + 1, x, y, aiDifficulty); // Passe la difficulté ici
            players.add(ia);
        }
    }

    private void clearSpawnZoneOnly(int x, int y) {
        if (grid.isInBounds(x, y)) {
            grid.setCell(x, y, Grid.CellType.EMPTY);
        }
    }

    public Grid getGrid() { return grid; }
    public List<Player> getPlayers() { return players; }
    public boolean isGameOver() { return gameOver; }
    public Player getWinner() { return winner; }
    public List<Bomb> getBombs() { return bombs; }
    public List<Bonus> getBonuses() { return bonuses; }

    public void updateAIs() {
        for (Player p : players) {
            if (p instanceof PlayerAI ai) {
                ai.updateAI(grid, bombs, players); // Passe la liste des joueurs pour comportement avancé
            }
        }
    }

    public void updateGameState() {
        int aliveCount = 0;
        Player lastAlive = null;
        for (Player p : players) {
            if (p.isAlive()) {
                aliveCount++;
                lastAlive = p;
            }
        }
        if (aliveCount <= 1) {
            gameOver = true;
            winner = lastAlive;
        }
    }

    public void movePlayer(Player player, int dx, int dy) {
        if (!gameOver && player.isAlive()) {
            player.move(dx, dy, grid);
        }
    }

    public void placeBomb(Player player) {
        if (!player.isAlive()) return;
        for (Bomb b : bombs) {
            if (b.getX() == player.getX() && b.getY() == player.getY()) {
                return;
            }
        }
        Bomb newBomb = new Bomb(player.getX(), player.getY(), Bomb.DEFAULT_TIMER, player.getBombRange(), player);
        if (newBomb != null) {
            bombs.add(newBomb);
            grid.setCell(player.getX(), player.getY(), Grid.CellType.BOMB);
        }
    }

    public void updateBombs() {
        Iterator<Bomb> it = bombs.iterator();
        while (it.hasNext()) {
            Bomb b = it.next();
            b.tick();
            if (b.isExploded()) {
                explode(b);
                it.remove();
            }
        }
        Iterator<Explosion> expIt = explosions.iterator();
        while (expIt.hasNext()) {
            Explosion exp = expIt.next();
            exp.ticksRemaining--;
            if (exp.ticksRemaining <= 0) {
                if (grid.getCell(exp.x, exp.y) == Grid.CellType.EXPLOSION) {
                    grid.setCell(exp.x, exp.y, Grid.CellType.EMPTY);
                }
                expIt.remove();
            }
        }
        for (Player p : players) {
            if (!p.isAlive()) continue;
            Iterator<Bonus> bonusIt = bonuses.iterator();
            while (bonusIt.hasNext()) {
                Bonus bonus = bonusIt.next();
                if (!bonus.isCollected()
                        && p.getX() == bonus.getX()
                        && p.getY() == bonus.getY()) {
                    bonus.applyTo(p);
                }
                if (bonus.isCollected()) {
                    bonusIt.remove();
                }
            }
        }
        for (Player p : players) {
            if (p.isAlive()) {
                p.updateActiveBonuses();
            }
        }
        // Nettoyage des centres d'explosion qui ne sont plus actifs (plus d'explosion à cet endroit)
        explosionCenters.removeIf(ec ->
                explosions.stream().noneMatch(exp -> exp.x == ec.x && exp.y == ec.y && exp.ticksRemaining > 0)
        );
        updateGameState();
    }

    // Ajout : incrémentation de la frame et enregistrement du centre
    private void addExplosion(int x, int y, boolean isCenter) {
        grid.setCell(x, y, Grid.CellType.EXPLOSION);
        explosions.add(new Explosion(x, y, 2));
        if (isCenter) {
            explosionCenters.add(new ExplosionCenter(x, y, explosionFrameCounter));
        }
    }
    private void explode(Bomb b) {
        int x = b.getX(), y = b.getY(), range = b.getRange();
        explosionFrameCounter++; // incrémente à chaque nouvelle explosion centrale
        addExplosion(x, y, true); // true => centre
        destroyWall(x, y);
        damagePlayersAt(x, y);

        int[][] dirs = {{1,0},{-1,0},{0,1},{0,-1}};
        for (int[] dir : dirs) {
            for (int i = 1; i <= range; i++) {
                int nx = x + dir[0]*i, ny = y + dir[1]*i;
                if (!grid.isInBounds(nx, ny)) break;
                Grid.CellType c = grid.getCell(nx, ny);
                if (c == Grid.CellType.INDESTRUCTIBLE) break;
                addExplosion(nx, ny, false); // false => branche
                damagePlayersAt(nx, ny);
                if (c == Grid.CellType.DESTRUCTIBLE) {
                    destroyWall(nx, ny);
                    break;
                }
            }
        }
    }
    private void damagePlayersAt(int x, int y) {
        for (Player p : players) {
            if (p.isAlive() && p.getX() == x && p.getY() == y) {
                p.takeDamage();
            }
        }
    }
    private void destroyWall(int x, int y) {
        if (grid.getCell(x, y) == Grid.CellType.DESTRUCTIBLE) {
            grid.setCell(x, y, Grid.CellType.EMPTY);
            if (Math.random() < 0.2) {
                bonuses.add(new FlameBonus(x, y, 1));
            }
        }
    }

    // ----------- NOUVEAU : pour affichage explosion façon Bomberman -----------
    /**
     * Retourne le type et la direction de la partie d'explosion pour la case (x, y), ou null si pas d'explosion.
     * Ne retourne CENTRE que pour la case où une bombe a explosé.
     * Jamais de branche ni de fin sur la case centre.
     */
    // Remplace uniquement la méthode getExplosionCell par celle-ci

    public ExplosionCell getExplosionCell(int x, int y) {
        if (grid.getCell(x, y) != Grid.CellType.EXPLOSION) return null;

        // Est-ce un centre d'explosion actuellement visible ?
        for (ExplosionCenter ec : explosionCenters) {
            if (ec.x == x && ec.y == y) {
                boolean stillAlive = false;
                for (Explosion exp : explosions) {
                    if (exp.x == x && exp.y == y && exp.ticksRemaining > 0) {
                        stillAlive = true;
                        break;
                    }
                }
                if (stillAlive) return new ExplosionCell(ExplosionPartType.CENTRE, null);
            }
        }

        // Est-ce une branche reliée à un centre dans une des 4 directions ?
        for (int d = 0; d < 4; d++) {
            int dx = 0, dy = 0;
            Direction dir = null;
            switch (d) {
                case 0 -> { dx = 1; dir = Direction.RIGHT; }
                case 1 -> { dx = -1; dir = Direction.LEFT; }
                case 2 -> { dy = 1; dir = Direction.DOWN; }
                case 3 -> { dy = -1; dir = Direction.UP; }
            }
            // On remonte jusqu'à trouver un centre, en s'arrêtant si mur ou case vide
            int cx = x;
            int cy = y;
            boolean foundCenter = false;
            int steps = 0;
            while (true) {
                cx -= dx;
                cy -= dy;
                steps++;
                if (!grid.isInBounds(cx, cy)) break;
                if (grid.getCell(cx, cy) != Grid.CellType.EXPLOSION) break;
                // Est-ce un centre actif ?
                for (ExplosionCenter ec : explosionCenters) {
                    if (ec.x == cx && ec.y == cy) {
                        boolean stillAlive = false;
                        for (Explosion exp : explosions) {
                            if (exp.x == cx && exp.y == cy && exp.ticksRemaining > 0) {
                                stillAlive = true;
                                break;
                            }
                        }
                        if (stillAlive) {
                            foundCenter = true;
                            break;
                        }
                    }
                }
                if (foundCenter) break;
            }
            if (foundCenter) {
                // On est bien dans une branche venant d'un centre, dans la direction d
                // Vérifie si c'est une fin : la suivante dans la même direction N'EST PAS explosion
                int nx = x + dx;
                int ny = y + dy;
                boolean isEnd = (!grid.isInBounds(nx, ny) || grid.getCell(nx, ny) != Grid.CellType.EXPLOSION);
                if (isEnd) {
                    return new ExplosionCell(ExplosionPartType.END, dir);
                } else {
                    return new ExplosionCell(ExplosionPartType.BRANCH, dir);
                }
            }
        }
        // Si aucune branche détectée, branche droite (par défaut, ne devrait jamais arriver)
        return new ExplosionCell(ExplosionPartType.BRANCH, Direction.RIGHT);
    }
    // --------------------------------------------------------------------------
}