package com.bomberman.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Game {
    private Grid grid;
    private List<Player> players;
    private boolean gameOver;
    private Player winner;

    private List<Bomb> bombs = new ArrayList<>();

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

    // Nouvelle structure pour gérer les briques endommagées
    private static class DamagedBrick {
        int x, y;
        int ticksRemaining;
        DamagedBrick(int x, int y, int ticksRemaining) {
            this.x = x;
            this.y = y;
            this.ticksRemaining = ticksRemaining;
        }
    }
    private List<DamagedBrick> damagedBricks = new ArrayList<>();

    public Game(int width, int height, int playerCount) {
        this.grid = new Grid(width, height);
        this.players = new ArrayList<>();
        this.gameOver = false;
        this.winner = null;
        initializePlayers(playerCount);
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
        if (!player.isAlive()) return; // Bloque la pose de bombe si joueur mort
        // Vérifie s'il n'y a pas déjà une bombe sur la case
        for (Bomb b : bombs)
            if (b.getX() == player.getX() && b.getY() == player.getY()) return;
        bombs.add(new Bomb(player.getX(), player.getY(), 3, 2)); // timer=3, range=2
        grid.setCell(player.getX(), player.getY(), Grid.CellType.BOMB);
    }

    public void updateBombs() {
        // Tick des bombes
        Iterator<Bomb> it = bombs.iterator();
        while (it.hasNext()) {
            Bomb b = it.next();
            b.tick();
            if (b.isExploded()) {
                explode(b);
                it.remove();
            }
        }

        // Tick des explosions (pour qu'elles restent affichées au moins 1 tick)
        Iterator<Explosion> expIt = explosions.iterator();
        while (expIt.hasNext()) {
            Explosion exp = expIt.next();
            exp.ticksRemaining--;
            if (exp.ticksRemaining <= 0) {
                // On nettoie la case
                if (grid.getCell(exp.x, exp.y) == Grid.CellType.EXPLOSION) {
                    grid.setCell(exp.x, exp.y, Grid.CellType.EMPTY);
                }
                expIt.remove();
            }
        }

        // Tick des briques endommagées
        Iterator<DamagedBrick> damagedIt = damagedBricks.iterator();
        while (damagedIt.hasNext()) {
            DamagedBrick damaged = damagedIt.next();
            damaged.ticksRemaining--;
            if (damaged.ticksRemaining <= 0) {
                // Détruit définitivement la brique
                if (grid.getCell(damaged.x, damaged.y) == Grid.CellType.DESTRUCTIBLE_DAMAGED) {
                    grid.setCell(damaged.x, damaged.y, Grid.CellType.EMPTY);
                }
                damagedIt.remove();
            }
        }
    }

    private void addExplosion(int x, int y) {
        if (grid.getCell(x, y) != Grid.CellType.DESTRUCTIBLE_DAMAGED) {
            grid.setCell(x, y, Grid.CellType.EXPLOSION);
            explosions.add(new Explosion(x, y, 2)); // 1 tick = visible pendant un cycle
        }
    }

    private void damageBrick(int x, int y) {
        if (grid.getCell(x, y) == Grid.CellType.DESTRUCTIBLE) {
            grid.setCell(x, y, Grid.CellType.DESTRUCTIBLE_DAMAGED);
            damagedBricks.add(new DamagedBrick(x, y, 2));
        }
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

                // Toujours endommager les joueurs
                damagePlayersAt(nx, ny);

                if (c == Grid.CellType.DESTRUCTIBLE) {
                    // Brique normale : on l'endommage SANS explosion
                    damageBrick(nx, ny);
                    break;
                } else if (c == Grid.CellType.DESTRUCTIBLE_DAMAGED) {
                    // Brique déjà endommagée : on la détruit avec explosion
                    addExplosion(nx, ny);
                    destroyWall(nx, ny);
                    break;
                } else {
                    // Case vide ou autres : explosion normale
                    addExplosion(nx, ny);
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
        Grid.CellType cellType = grid.getCell(x, y);
        if (cellType == Grid.CellType.DESTRUCTIBLE || cellType == Grid.CellType.DESTRUCTIBLE_DAMAGED) {
            grid.setCell(x, y, Grid.CellType.EMPTY);
            // Supprime la brique de la liste des briques endommagées si elle y était
            damagedBricks.removeIf(damaged -> damaged.x == x && damaged.y == y);
        }
    }
}