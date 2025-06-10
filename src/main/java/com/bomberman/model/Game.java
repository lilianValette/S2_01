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
        updateGameState();
    }

    private void addExplosion(int x, int y) {
        grid.setCell(x, y, Grid.CellType.EXPLOSION);
        explosions.add(new Explosion(x, y, 2));
    }
    private void explode(Bomb b) {
        int x = b.getX(), y = b.getY(), range = b.getRange();
        addExplosion(x, y);
        //destroyWall(x, y);
        damagePlayersAt(x, y);

        int[][] dirs = {{1,0},{-1,0},{0,1},{0,-1}};
        for (int[] dir : dirs) {
            for (int i = 1; i <= range; i++) {
                int nx = x + dir[0]*i, ny = y + dir[1]*i;
                if (!grid.isInBounds(nx, ny)) break;
                Grid.CellType c = grid.getCell(nx, ny);
                if (c == Grid.CellType.INDESTRUCTIBLE) break;
                addExplosion(nx, ny);
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
                if (!p.isInvincibleToBombs()) {
                    p.takeDamage();
                }
            }
        }
    }
    private void destroyWall(int x, int y) {
        if (Math.random() < 0.2) {
            int bonusType = (int) (Math.random() * 3);
            switch (bonusType) {
                case 0 -> bonuses.add(new FlameBonus(x, y, 1));
                case 1 -> bonuses.add(new JacketBonus(x, y));
                case 2 -> bonuses.add(new LifeBonus(x, y));
            }
        }
    }
}