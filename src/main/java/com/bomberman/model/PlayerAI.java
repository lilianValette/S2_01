package com.bomberman.model;

import java.util.*;

public class PlayerAI extends Player {
    private final Random random = new Random();
    private final AIDifficulty difficulty;

    // Pour savoir si l'IA doit fuir sa bombe (true = on vient de poser une bombe et on doit fuir)
    private boolean mustFleeOwnBomb = false;
    private int lastBombX = -1, lastBombY = -1, lastBombTimer = -1;

    public PlayerAI(int id, int startX, int startY, AIDifficulty difficulty) {
        super(id, startX, startY, false);
        this.difficulty = difficulty;
    }

    public AIDifficulty getDifficulty() { return difficulty; }

    // Ignore la bombe que l'IA vient juste de poser si demandé
    private int dangerLevelAt(int x, int y, List<Bomb> bombs, Grid grid, boolean ignoreOwnLastBomb) {
        int minTick = Integer.MAX_VALUE;
        for (Bomb bomb : bombs) {
            int bx = bomb.getX(), by = bomb.getY(), range = bomb.getRange();
            int timer = bomb.getTimer();
            if (ignoreOwnLastBomb && bx == lastBombX && by == lastBombY && timer == lastBombTimer) continue;
            if (x == bx && y == by) minTick = Math.min(minTick, timer);
            if (y == by && Math.abs(x - bx) <= range) {
                boolean blocked = false;
                for (int ix = Math.min(x, bx) + 1; ix < Math.max(x, bx); ix++) {
                    if (grid.getCell(ix, y) == Grid.CellType.INDESTRUCTIBLE) { blocked = true; break; }
                }
                if (!blocked) minTick = Math.min(minTick, timer);
            }
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

    public void updateAI(Grid grid, List<Bomb> bombs, List<Player> allPlayers) {
        if (!isAlive()) return;
        switch (difficulty) {
            case FACILE -> updateEasyAI(grid, bombs);
            case NORMAL -> updateNormalAI(grid, bombs);
            case DIFFICILE -> updateHardAI(grid, bombs, allPlayers);
        }
    }

    // --- FACILE ---
    private void updateEasyAI(Grid grid, List<Bomb> bombs) {
        int curX = getX(), curY = getY();
        if (random.nextDouble() < 0.3) return;
        if (random.nextDouble() < 0.2) {
            Bomb bomb = dropBomb(Bomb.DEFAULT_TIMER, bombs);
            if (bomb != null) {
                bombs.add(bomb);
                mustFleeOwnBomb = true;
                lastBombX = curX;
                lastBombY = curY;
                lastBombTimer = bomb.getTimer();
            }
        }
        if (mustFleeOwnBomb) {
            fleeOwnBomb(grid, bombs, curX, curY);
            return;
        }
        int myDanger = dangerLevelAt(curX, curY, bombs, grid, false);
        if (myDanger < Integer.MAX_VALUE && random.nextDouble() < 0.6) {
            tryMoveToSafeNeighbour(grid, bombs, curX, curY);
        } else {
            tryMoveToSafeNeighbour(grid, bombs, curX, curY);
        }
    }

    // --- NORMAL ---
    private boolean mustFleeOwnBombNormal = false;

    private void updateNormalAI(Grid grid, List<Bomb> bombs) {
        int curX = getX(), curY = getY();
        if (mustFleeOwnBombNormal) {
            fleeOwnBombNormal(grid, bombs, curX, curY);
            return;
        }
        int myDanger = dangerLevelAt(curX, curY, bombs, grid, false);
        if (myDanger < Integer.MAX_VALUE) {
            tryMoveToSafeNeighbour(grid, bombs, curX, curY);
            return;
        }
        if (random.nextDouble() < 0.12) {
            Bomb bomb = dropBomb(Bomb.DEFAULT_TIMER, bombs);
            if (bomb != null) {
                bombs.add(bomb);
                mustFleeOwnBombNormal = true;
                lastBombX = curX;
                lastBombY = curY;
                lastBombTimer = bomb.getTimer();
                fleeOwnBombNormal(grid, bombs, curX, curY);
                return;
            }
        }
        tryMoveToSafeNeighbour(grid, bombs, curX, curY);
    }

    // Pour la normale, copie la même logique de fuite que la difficile
    private boolean fleeOwnBombNormal(Grid grid, List<Bomb> bombs, int curX, int curY) {
        int[][] directions = {{0, 1}, {0, -1}, {-1, 0}, {1, 0}};
        List<int[]> escapeDirs = new ArrayList<>();
        for (int[] dir : directions) {
            int nx = curX + dir[0], ny = curY + dir[1];
            if (grid.isInBounds(nx, ny)
                    && grid.getCell(nx, ny) == Grid.CellType.EMPTY
                    && dangerLevelAt(nx, ny, bombs, grid, true) == Integer.MAX_VALUE) {
                escapeDirs.add(dir);
            }
        }
        if (!escapeDirs.isEmpty()) {
            int[] dir = escapeDirs.get(random.nextInt(escapeDirs.size()));
            move(dir[0], dir[1], grid);
            if (dangerLevelAt(getX(), getY(), bombs, grid, false) == Integer.MAX_VALUE) {
                mustFleeOwnBombNormal = false; // on a fui !
            }
            return true;
        }
        // Sinon, tente la case la moins risquée
        int bestDanger = Integer.MIN_VALUE;
        int[] bestDir = null;
        for (int[] dir : directions) {
            int nx = curX + dir[0], ny = curY + dir[1];
            if (grid.isInBounds(nx, ny) && grid.getCell(nx, ny) == Grid.CellType.EMPTY) {
                int danger = dangerLevelAt(nx, ny, bombs, grid, true);
                if (danger > bestDanger) {
                    bestDanger = danger;
                    bestDir = dir;
                }
            }
        }
        if (bestDir != null) {
            move(bestDir[0], bestDir[1], grid);
        }
        if (dangerLevelAt(getX(), getY(), bombs, grid, false) == Integer.MAX_VALUE) {
            mustFleeOwnBombNormal = false;
        }
        return false;
    }

    // --- DIFFICILE ---
    private void updateHardAI(Grid grid, List<Bomb> bombs, List<Player> allPlayers) {
        int curX = getX(), curY = getY();

        // 1. Fuite prioritaire de sa bombe
        if (mustFleeOwnBomb) {
            fleeOwnBomb(grid, bombs, curX, curY);
            return;
        }

        int myDanger = dangerLevelAt(curX, curY, bombs, grid, false);
        if (myDanger < Integer.MAX_VALUE) {
            tryMoveToSafeNeighbour(grid, bombs, curX, curY);
            return;
        }

        // --- MODIFIE ICI : CIBLE LE JOUEUR LE PLUS PROCHE, QUEL QU'IL SOIT ---
        Player target = null;
        int targetDist = Integer.MAX_VALUE;
        for (Player p : allPlayers) {
            if (p != this && p.isAlive()) { // On ne cible que les autres vivants, humain ou IA
                int dist = Math.abs(p.getX() - curX) + Math.abs(p.getY() - curY);
                if (dist < targetDist) {
                    targetDist = dist; target = p;
                }
            }
        }

        // Si à portée de bombe, attaque mais vérifie la fuite (robuste)
        if (target != null && targetDist <= 2) {
            boolean alreadyBomb = false;
            for (Bomb b : bombs)
                if (b.getX() == curX && b.getY() == curY) alreadyBomb = true;
            if (!alreadyBomb) {
                boolean canEscape = canReallyEscapeAfterBomb(grid, bombs, curX, curY, Bomb.DEFAULT_TIMER);
                if (canEscape || random.nextDouble() < 0.08) {
                    Bomb bomb = dropBomb(Bomb.DEFAULT_TIMER, bombs);
                    if (bomb != null) {
                        bombs.add(bomb);
                        mustFleeOwnBomb = true;
                        lastBombX = curX;
                        lastBombY = curY;
                        lastBombTimer = bomb.getTimer();
                    }
                }
            }
            // On ne poursuit pas le joueur, on s'occupe de la fuite !
            if (mustFleeOwnBomb) {
                fleeOwnBomb(grid, bombs, curX, curY);
            }
            return;
        }

        // Sinon, se rapproche de la cible (A*) mais NE BOUGE que vers une case sûre
        if (target != null) {
            int[] nextMove = findPathToTargetOrDestructible(grid, bombs, curX, curY, target.getX(), target.getY());
            if (nextMove != null) {
                int nx = curX + nextMove[0], ny = curY + nextMove[1];
                if (dangerLevelAt(nx, ny, bombs, grid, false) == Integer.MAX_VALUE) {
                    move(nextMove[0], nextMove[1], grid);
                    // Si il bloque sur un mur destructible, pose une bombe mais vérifie la fuite (robuste)
                    if (grid.getCell(nx, ny) == Grid.CellType.DESTRUCTIBLE) {
                        boolean alreadyBomb = false;
                        for (Bomb b : bombs)
                            if (b.getX() == curX && b.getY() == curY) alreadyBomb = true;
                        if (!alreadyBomb) {
                            boolean canEscape = canReallyEscapeAfterBomb(grid, bombs, curX, curY, Bomb.DEFAULT_TIMER);
                            if (canEscape || random.nextDouble() < 0.08) {
                                Bomb bomb = dropBomb(Bomb.DEFAULT_TIMER, bombs);
                                if (bomb != null) {
                                    bombs.add(bomb);
                                    mustFleeOwnBomb = true;
                                    lastBombX = curX;
                                    lastBombY = curY;
                                    lastBombTimer = bomb.getTimer();
                                }
                            }
                        }
                    }
                }
                return;
            }
        }

        // Si bloqué, tente de casser un bloc adjacent (mais vérifie la fuite)
        boolean bombed = false;
        int[][] dirs = {{1,0},{-1,0},{0,1},{0,-1}};
        for (int[] dir : dirs) {
            int nx = curX + dir[0], ny = curY + dir[1];
            if (grid.isInBounds(nx, ny) && grid.getCell(nx, ny) == Grid.CellType.DESTRUCTIBLE) {
                boolean alreadyBomb = false;
                for (Bomb b : bombs)
                    if (b.getX() == curX && b.getY() == curY) alreadyBomb = true;
                if (!alreadyBomb) {
                    boolean canEscape = canReallyEscapeAfterBomb(grid, bombs, curX, curY, Bomb.DEFAULT_TIMER);
                    if (canEscape || random.nextDouble() < 0.08) {
                        Bomb bomb = dropBomb(Bomb.DEFAULT_TIMER, bombs);
                        if (bomb != null) {
                            bombs.add(bomb);
                            mustFleeOwnBomb = true;
                            lastBombX = curX;
                            lastBombY = curY;
                            lastBombTimer = bomb.getTimer();
                        }
                        bombed = true;
                    }
                }
            }
        }
        if (bombed) {
            fleeOwnBomb(grid, bombs, curX, curY);
            return;
        }

        tryMoveToSafeNeighbour(grid, bombs, curX, curY);
    }

    // Fuite intelligente de SA bombe (ignore la bombe qu'on vient de poser)
    private boolean fleeOwnBomb(Grid grid, List<Bomb> bombs, int curX, int curY) {
        int[][] directions = {{0, 1}, {0, -1}, {-1, 0}, {1, 0}};
        List<int[]> escapeDirs = new ArrayList<>();
        for (int[] dir : directions) {
            int nx = curX + dir[0], ny = curY + dir[1];
            // IGNORE SA PROPRE BOMBE dans le danger
            if (grid.isInBounds(nx, ny)
                    && grid.getCell(nx, ny) == Grid.CellType.EMPTY
                    && dangerLevelAt(nx, ny, bombs, grid, true) == Integer.MAX_VALUE) {
                escapeDirs.add(dir);
            }
        }
        if (!escapeDirs.isEmpty()) {
            int[] dir = escapeDirs.get(random.nextInt(escapeDirs.size()));
            move(dir[0], dir[1], grid);
            // Vérifie si on est enfin sorti de la zone de danger de SA bombe
            if (dangerLevelAt(getX(), getY(), bombs, grid, false) == Integer.MAX_VALUE) {
                mustFleeOwnBomb = false; // On n'est plus dans la zone de danger
            }
            return true;
        }
        // Si pas de case sûre ignorée, alors tente une case moins dangereuse (priorité à la fuite)
        int bestDanger = Integer.MIN_VALUE;
        int[] bestDir = null;
        for (int[] dir : directions) {
            int nx = curX + dir[0], ny = curY + dir[1];
            if (grid.isInBounds(nx, ny) && grid.getCell(nx, ny) == Grid.CellType.EMPTY) {
                int danger = dangerLevelAt(nx, ny, bombs, grid, true);
                if (danger > bestDanger) {
                    bestDanger = danger;
                    bestDir = dir;
                }
            }
        }
        if (bestDir != null) {
            move(bestDir[0], bestDir[1], grid);
        }
        if (dangerLevelAt(getX(), getY(), bombs, grid, false) == Integer.MAX_VALUE) {
            mustFleeOwnBomb = false;
        }
        return false;
    }

    // Déplacement normal (case vraiment sûre)
    private boolean tryMoveToSafeNeighbour(Grid grid, List<Bomb> bombs, int curX, int curY) {
        int[][] directions = {{0, 1}, {0, -1}, {-1, 0}, {1, 0}};
        List<int[]> choices = new ArrayList<>();
        for (int[] dir : directions) {
            int nx = curX + dir[0], ny = curY + dir[1];
            if (grid.isInBounds(nx, ny)
                    && grid.getCell(nx, ny) == Grid.CellType.EMPTY
                    && dangerLevelAt(nx, ny, bombs, grid, false) == Integer.MAX_VALUE) {
                choices.add(dir);
            }
        }
        if (!choices.isEmpty()) {
            int[] dir = choices.get(random.nextInt(choices.size()));
            move(dir[0], dir[1], grid);
            return true;
        }
        return false;
    }

    // Vérifie si l'IA peut atteindre une case sûre AVANT que la bombe n'explose
    private boolean canReallyEscapeAfterBomb(Grid grid, List<Bomb> bombs, int startX, int startY, int bombTimer) {
        int[][] directions = {{1,0},{-1,0},{0,1},{0,-1}};
        List<Bomb> bombsWithNew = new ArrayList<>(bombs);
        bombsWithNew.add(new Bomb(startX, startY, bombTimer, getBombRange(), this));

        Queue<int[]> queue = new ArrayDeque<>();
        Set<String> visited = new HashSet<>();
        queue.add(new int[]{startX, startY, 0});
        visited.add(startX + "," + startY + ",0");

        while (!queue.isEmpty()) {
            int[] node = queue.poll();
            int x = node[0], y = node[1], tick = node[2];
            // Pour la recherche d'échappatoire, on ignore la bombe fraîchement posée
            if (tick < bombTimer && dangerLevelAt(x, y, bombsWithNew, grid, true) == Integer.MAX_VALUE) {
                return true;
            }
            if (tick >= bombTimer) continue;
            for (int[] dir : directions) {
                int nx = x + dir[0], ny = y + dir[1];
                String state = nx + "," + ny + "," + (tick + 1);
                if (grid.isInBounds(nx, ny)
                        && grid.getCell(nx, ny) == Grid.CellType.EMPTY
                        && !visited.contains(state)) {
                    if (!(nx == startX && ny == startY && tick > 0)) {
                        visited.add(state);
                        queue.add(new int[]{nx, ny, tick + 1});
                    }
                }
            }
        }
        return false;
    }

    // Trouve un chemin vers la cible (A*) ou vers un bloc destructible si bloqué
    private int[] findPathToTargetOrDestructible(Grid grid, List<Bomb> bombs, int startX, int startY, int goalX, int goalY) {
        int[][] directions = {{1,0},{-1,0},{0,1},{0,-1}};
        int w = grid.getWidth(), h = grid.getHeight();
        boolean[][] visited = new boolean[w][h];
        PriorityQueue<Node> queue = new PriorityQueue<>(Comparator.comparingInt(n -> n.fCost));
        queue.add(new Node(startX, startY, null, 0, Math.abs(startX-goalX)+Math.abs(startY-goalY)));
        while (!queue.isEmpty()) {
            Node node = queue.poll();
            if (node.x == goalX && node.y == goalY) {
                Node prev = node;
                while (prev.parent != null && prev.parent.parent != null)
                    prev = prev.parent;
                return new int[]{prev.x - startX, prev.y - startY};
            }
            visited[node.x][node.y] = true;
            for (int[] dir : directions) {
                int nx = node.x + dir[0], ny = node.y + dir[1];
                if (grid.isInBounds(nx, ny) && !visited[nx][ny]) {
                    Grid.CellType cell = grid.getCell(nx, ny);
                    if (cell == Grid.CellType.EMPTY || cell == Grid.CellType.DESTRUCTIBLE) {
                        int gCost = node.gCost + 1;
                        int hCost = Math.abs(nx - goalX) + Math.abs(ny - goalY);
                        queue.add(new Node(nx, ny, node, gCost, hCost));
                    }
                }
            }
        }
        for (int[] dir : directions) {
            int nx = startX + dir[0], ny = startY + dir[1];
            if (grid.isInBounds(nx, ny) && grid.getCell(nx, ny) == Grid.CellType.DESTRUCTIBLE) {
                return dir;
            }
        }
        return null;
    }

    private static class Node {
        int x, y, gCost, hCost, fCost;
        Node parent;
        Node(int x, int y, Node parent, int gCost, int hCost) {
            this.x = x; this.y = y; this.parent = parent;
            this.gCost = gCost; this.hCost = hCost; this.fCost = gCost + hCost;
        }
    }
}