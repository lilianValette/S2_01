package com.bomberman.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Joueur IA avancée pour Bomberman : fuite intelligente des bombes, moins de blocages.
 */
public class PlayerAI extends Player {
    private final Random random = new Random();

    // Pour mémoriser la dernière bombe posée
    private int lastBombX = -1;
    private int lastBombY = -1;
    private int lastBombTick = -1; // timer initial

    private boolean hasJustPlacedBomb = false;

    public PlayerAI(int id, int startX, int startY) {
        super(id, startX, startY, false);
    }

    /**
     * Retourne le nombre de ticks avant explosion si la case est dans la zone d'explosion d'une bombe,
     * sinon Integer.MAX_VALUE.
     */
    private int dangerLevelAt(int x, int y, List<Bomb> bombs, Grid grid, boolean ignoreJustPlacedBomb) {
        int minTick = Integer.MAX_VALUE;
        for (Bomb bomb : bombs) {
            if (ignoreJustPlacedBomb && bomb.getX() == lastBombX && bomb.getY() == lastBombY && bomb.getTimer() == lastBombTick) {
                continue;
            }
            int bx = bomb.getX(), by = bomb.getY(), range = bomb.getRange();
            int timer = bomb.getTimer();

            if (x == bx && y == by) minTick = Math.min(minTick, timer);

            // Même ligne
            if (y == by && Math.abs(x - bx) <= range) {
                boolean blocked = false;
                for (int ix = Math.min(x, bx) + 1; ix < Math.max(x, bx); ix++) {
                    if (grid.getCell(ix, y) == Grid.CellType.INDESTRUCTIBLE) { blocked = true; break; }
                }
                if (!blocked) minTick = Math.min(minTick, timer);
            }
            // Même colonne
            if (x == bx && Math.abs(y - by) <= range) {
                boolean blocked = false;
                for (int iy = Math.min(y, by) + 1; iy < Math.max(y, by); iy++) {
                    if (grid.getCell(x, iy) == Grid.CellType.INDESTRUCTIBLE) { blocked = true; break; }
                }
                if (!blocked) minTick = Math.min(minTick, timer);
            }
        }
        return minTick;
    }

    public void updateAI(Grid grid, List<Bomb> bombs) {
        if (!isAlive()) return;

        int curX = getX(), curY = getY();
        boolean ignoreJustPlacedBomb = hasJustPlacedBomb;

        int myDanger = dangerLevelAt(curX, curY, bombs, grid, ignoreJustPlacedBomb);

        // 1. FUITE : Si en danger, cherche la case la moins dangereuse autour
        if (myDanger < Integer.MAX_VALUE) {
            int[][] directions = { {0,1}, {0,-1}, {-1,0}, {1,0} };
            List<int[]> bestDirs = new ArrayList<>();
            int bestDanger = myDanger;

            for (int[] dir : directions) {
                int nx = curX + dir[0], ny = curY + dir[1];
                if (grid.isInBounds(nx, ny) && grid.getCell(nx, ny) == Grid.CellType.EMPTY) {
                    int d = ignoreJustPlacedBomb ? Integer.MAX_VALUE : dangerLevelAt(nx, ny, bombs, grid, false);
                    if (d > bestDanger) { // On a trouvé une case moins dangereuse
                        bestDirs.clear();
                        bestDirs.add(dir);
                        bestDanger = d;
                    } else if (d == bestDanger) {
                        bestDirs.add(dir);
                    }
                }
            }
            if (!bestDirs.isEmpty()) {
                int[] dir = bestDirs.get(random.nextInt(bestDirs.size()));
                move(dir[0], dir[1], grid);
                hasJustPlacedBomb = false;
                return;
            }
            hasJustPlacedBomb = false;
            return;
        }

        // 2. Déplacement normal (favorise cases sûres)
        int[][] directions = { {0, 1}, {0, -1}, {-1, 0}, {1, 0} };
        List<int[]> safeDirs = new ArrayList<>();
        for (int[] dir : directions) {
            int nx = curX + dir[0], ny = curY + dir[1];
            if (grid.isInBounds(nx, ny) && grid.getCell(nx, ny) == Grid.CellType.EMPTY
                    && dangerLevelAt(nx, ny, bombs, grid, false) == Integer.MAX_VALUE) {
                safeDirs.add(dir);
            }
        }
        if (!safeDirs.isEmpty() && random.nextDouble() < 0.7) {
            int[] dir = safeDirs.get(random.nextInt(safeDirs.size()));
            move(dir[0], dir[1], grid);
        }

        // 3. Pose une bombe avec proba, mais seulement si possible de fuir ensuite
        if (random.nextDouble() < 0.08 && myDanger == Integer.MAX_VALUE) {
            // Est-ce qu'il y a au moins une case sûre à côté ?
            List<int[]> escapeDirs = new ArrayList<>();
            for (int[] dir : directions) {
                int nx = curX + dir[0], ny = curY + dir[1];
                if (grid.isInBounds(nx, ny) && grid.getCell(nx, ny) == Grid.CellType.EMPTY
                        && dangerLevelAt(nx, ny, bombs, grid, false) == Integer.MAX_VALUE) {
                    escapeDirs.add(dir);
                }
            }
            if (!escapeDirs.isEmpty()) {
                Bomb bomb = dropBomb(Bomb.DEFAULT_TIMER, bombs);
                if (bomb != null) {
                    bombs.add(bomb);
                    lastBombX = curX;
                    lastBombY = curY;
                    lastBombTick = bomb.getTimer();
                    hasJustPlacedBomb = true;
                }
            }
        }
    }
}