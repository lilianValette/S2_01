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

    public Game(int width, int height, int playerCount, int iaCount, Theme theme) {
        this.grid = new Grid(width, height, theme);
        this.players = new ArrayList<>();
        this.gameOver = false;
        this.winner = null;
        int totalPlayers = playerCount + iaCount;
        initializePlayers(totalPlayers, playerCount);
    }

    // Sécurise le spawn : le joueur spawn toujours sur du sol, jamais sur un bloc, et toutes les cases autour (y compris diagonales) sont mises à EMPTY
    private void initializePlayers(int totalPlayers, int humanCount) {
        int[][] startPositions = {
                {1, 1},
                {grid.getWidth() - 2, grid.getHeight() - 2},
                {1, grid.getHeight() - 2},
                {grid.getWidth() - 2, 1}
        };
        for (int i = 0; i < totalPlayers && i < startPositions.length; i++) {
            int x = startPositions[i][0];
            int y = startPositions[i][1];

            // S'assure que la case de spawn et les 8 alentours (croix + diagonales) sont du sol (EMPTY)
            clearSpawnZoneWithDiagonals(x, y);

            boolean isHuman = i < humanCount;
            Player p = new Player(i + 1, x, y, isHuman);
            players.add(p);
        }
    }

    // Met la case de spawn et toutes ses cases adjacentes (y compris diagonales) à EMPTY
    private void clearSpawnZoneWithDiagonals(int x, int y) {
        int[][] dirs = {
                {0,0},
                {0,1}, {0,-1}, {1,0}, {-1,0},
                {1,1}, {1,-1}, {-1,1}, {-1,-1}
        };
        for (int[] d : dirs) {
            int nx = x + d[0], ny = y + d[1];
            if (grid.isInBounds(nx, ny)) {
                grid.setCell(nx, ny, Grid.CellType.EMPTY);
            }
        }
    }

    public Grid getGrid() { return grid; }
    public List<Player> getPlayers() { return players; }
    public boolean isGameOver() { return gameOver; }
    public Player getWinner() { return winner; }
    public List<Bomb> getBombs() { return bombs; }

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
        for (Bomb b : bombs)
            if (b.getX() == player.getX() && b.getY() == player.getY()) return;
        bombs.add(new Bomb(player.getX(), player.getY(), 3, 2));
        grid.setCell(player.getX(), player.getY(), Grid.CellType.BOMB);
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
    }

    private void addExplosion(int x, int y) {
        grid.setCell(x, y, Grid.CellType.EXPLOSION);
        explosions.add(new Explosion(x, y, 2));
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
        if (grid.getCell(x, y) == Grid.CellType.DESTRUCTIBLE)
            grid.setCell(x, y, Grid.CellType.EMPTY);
    }
}