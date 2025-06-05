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
        // Vérifie s’il n’y a pas déjà une bombe sur la case
        for (Bomb b : bombs)
            if (b.getX() == player.getX() && b.getY() == player.getY()) return;
        bombs.add(new Bomb(player.getX(), player.getY(), 3, 2)); // timer=3, range=2
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
        // Nettoyage des explosions temporaires
        for (int y = 0; y < grid.getHeight(); y++) {
            for (int x = 0; x < grid.getWidth(); x++) {
                if (grid.getCell(x, y) == Grid.CellType.EXPLOSION) {
                    grid.setCell(x, y, Grid.CellType.EMPTY);
                }
            }
        }
        // Remettre la case à EMPTY après explosion si elle n'est plus utilisée
        for (Bomb b : bombs) {
            if (grid.getCell(b.getX(), b.getY()) == Grid.CellType.BOMB) {
                // On laisse la bombe affichée tant qu'elle n'a pas explosé
            }
        }
    }

    private void explode(Bomb b) {
        int x = b.getX(), y = b.getY(), range = b.getRange();
        grid.setCell(x, y, Grid.CellType.EXPLOSION);
        destroyWall(x, y);
        damagePlayersAt(x, y);

        int[][] dirs = {{1,0},{-1,0},{0,1},{0,-1}};
        for (int[] dir : dirs) {
            for (int i = 1; i <= range; i++) {
                int nx = x + dir[0]*i, ny = y + dir[1]*i;
                if (!grid.isInBounds(nx, ny)) break;
                Grid.CellType c = grid.getCell(nx, ny);
                if (c == Grid.CellType.INDESTRUCTIBLE) break;
                grid.setCell(nx, ny, Grid.CellType.EXPLOSION);
                damagePlayersAt(nx, ny);
                if (c == Grid.CellType.DESTRUCTIBLE) {
                    destroyWall(nx, ny);
                    break;
                }
            }
        }
        grid.setCell(x, y, Grid.CellType.EMPTY);
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