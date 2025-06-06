package com.bomberman.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import com.bomberman.model.Bonus;      // AJOUT BONUS : import de la classe abstraite
import com.bomberman.model.FlameBonus; // AJOUT BONUS : import du bonus concret

public class Game {
    private Grid grid;
    private List<Player> players;
    private boolean gameOver;
    private Player winner;

    private List<Bomb> bombs = new ArrayList<>();
    // AJOUT BONUS : liste des bonus présents sur la map
    private List<Bonus> bonuses = new ArrayList<>();


    // Nouvelle structure pour garder les explosions visibles
    private static class Explosion {
        int x, y;
        int ticksRemaining;
        Explosion(int x, int y, int ticksRemaining) {
            this.x = x;
            this.y = y;
            this.ticksRemaining = ticksRemaining;
        }
    }
    private List<Explosion> explosions = new ArrayList<>();

    public Game(int width, int height, int playerCount) {
        this.grid = new Grid(width, height);
        this.players = new ArrayList<>();
        this.gameOver = false;
        this.winner = null;
        initializePlayers(playerCount);

        // AJOUT BONUS : on place déjà un bonus fixe pour tester
        bonuses.add(new FlameBonus(5, 3, 1));
    }

    private void initializePlayers(int playerCount) {
        int[][] startPositions = {
                {1, 1},
                {grid.getWidth() - 2, grid.getHeight() - 2},
                {1, grid.getHeight() - 2},
                {grid.getWidth() - 2, 1}
        };
        for (int i = 0; i < playerCount && i < startPositions.length; i++) {
            Player p = new Player(i + 1, startPositions[i][0], startPositions[i][1]);
            players.add(p);
        }
    }

    public Grid getGrid() {
        return grid;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public Player getWinner() {
        return winner;
    }

    public List<Bomb> getBombs() {
        return bombs;
    }

    /** AJOUT BONUS : permet au controller de récupérer la liste des bonus pour le dessin */
    public List<Bonus> getBonuses() {
        return bonuses;
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

        Bomb newBomb = player.dropBomb(3, bombs);
        if (newBomb != null) {
            bombs.add(newBomb);
            grid.setCell(player.getX(), player.getY(), Grid.CellType.BOMB);
        }
    }


    public void updateBombs() {
        // 1) Tick des bombes et explosions (votre code existant) …
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

        // 2) Ramassage des bonus au sol
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

        // 3)  mise à jour des bonus actifs (durée en secondes)
        for (Player p : players) {
            if (p.isAlive()) {
                p.updateActiveBonuses();
            }
        }

        // 4) Mise à jour du statut de fin de partie
        updateGameState();
    }



    private void addExplosion(int x, int y) {
        grid.setCell(x, y, Grid.CellType.EXPLOSION);
        explosions.add(new Explosion(x, y, 2)); // 1 tick = visible pendant un cycle
    }

    private void explode(Bomb b) {
        int x = b.getX(), y = b.getY(), range = b.getRange();
        addExplosion(x, y);
        destroyWall(x, y);
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
                p.takeDamage();
            }
        }
    }

    private void destroyWall(int x, int y) {
        if (grid.getCell(x, y) == Grid.CellType.DESTRUCTIBLE) {
            grid.setCell(x, y, Grid.CellType.EMPTY);

            // AJOUT BONUS : 20 % de chances de faire apparaître un FlameBonus à cet endroit
            if (Math.random() < 0.2) {
                bonuses.add(new FlameBonus(x, y, 1));
            }
        }
    }

}