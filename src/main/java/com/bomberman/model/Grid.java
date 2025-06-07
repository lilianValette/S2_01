package com.bomberman.model;

public class Grid {
    public enum CellType {
        EMPTY,             // Case vide
        INDESTRUCTIBLE,    // Mur indestructible (bordure ou obstacle central)
        DESTRUCTIBLE,      // Mur destructible (peut être détruit par une bombe)
        DESTRUCTIBLE_DAMAGED, // Mur destructible endommagé (n-bricks)
        BOMB,              // Bombe présente sur la case
        EXPLOSION,         // Explosion temporaire (pour l'affichage)
        PLAYER1,           // Optionnel : présence d'un joueur (pour affichage)
        PLAYER2            // Optionnel : présence d'un joueur (pour affichage)
    }

    private final int width;
    private final int height;
    private final CellType[][] cells;

    public Grid(int width, int height) {
        this.width = width;
        this.height = height;
        cells = new CellType[height][width];
        initializeGrid();
    }

    private void initializeGrid() {
        // Remplir la grille avec des murs indestructibles sur le bord, et des murs destructibles à l'intérieur
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (isBorder(x, y) || isCentralWall(x, y)) {
                    cells[y][x] = CellType.INDESTRUCTIBLE;
                } else if (Math.random() < 0.3) {
                    cells[y][x] = CellType.DESTRUCTIBLE;
                } else {
                    cells[y][x] = CellType.EMPTY;
                }
            }
        }
        // Tu peux ici libérer les positions de départ des joueurs si besoin (ex : coins)
        clearStartPositions();
    }

    private boolean isBorder(int x, int y) {
        return x == 0 || y == 0 || x == width - 1 || y == height - 1;
    }

    // Optionnel : murs centraux fixes (ex: un sur deux)
    private boolean isCentralWall(int x, int y) {
        // Exemple : tous les deux en x et y, sauf les bords
        return (x % 2 == 0 && y % 2 == 0) && !isBorder(x, y);
    }

    private void clearStartPositions() {
        // Libère les cases de départ des joueurs (coins de la grille)
        cells[1][1] = CellType.EMPTY;
        cells[1][2] = CellType.EMPTY;
        cells[2][1] = CellType.EMPTY;
        cells[height - 2][width - 2] = CellType.EMPTY;
        cells[height - 2][width - 3] = CellType.EMPTY;
        cells[height - 3][width - 2] = CellType.EMPTY;
    }

    public CellType getCell(int x, int y) {
        if (isInBounds(x, y)) {
            return cells[y][x];
        }
        return null;
    }

    public void setCell(int x, int y, CellType type) {
        if (isInBounds(x, y)) {
            cells[y][x] = type;
        }
    }

    public boolean isInBounds(int x, int y) {
        return x >= 0 && x < width && y >= 0 && y < height;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}