package com.bomberman.model;

public class Grid {
    public enum CellType {
        EMPTY,
        INDESTRUCTIBLE,
        DESTRUCTIBLE,
        BOMB,
        EXPLOSION
    }

    private final int width;
    private final int height;
    private final CellType[][] cells;

    public Grid(int width, int height, Theme theme) {
        this.width = width;
        this.height = height;
        cells = new CellType[width][height];

        int[][] layout = theme != null ? theme.getPreviewLayout() : null;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (layout != null && y < layout.length && x < layout[0].length) {
                    switch (layout[y][x]) {
                        case 1 -> cells[x][y] = CellType.INDESTRUCTIBLE;
                        case 2 -> cells[x][y] = CellType.DESTRUCTIBLE;
                        default -> cells[x][y] = CellType.EMPTY;
                    }
                } else {
                    if (x == 0 || y == 0 || x == width - 1 || y == height - 1) {
                        cells[x][y] = CellType.INDESTRUCTIBLE;
                    } else if (x % 2 == 0 && y % 2 == 0) {
                        cells[x][y] = CellType.INDESTRUCTIBLE;
                    } else if (Math.random() < 0.2) {
                        cells[x][y] = CellType.DESTRUCTIBLE;
                    } else {
                        cells[x][y] = CellType.EMPTY;
                    }
                }
            }
        }
    }

    public int getWidth() { return width; }
    public int getHeight() { return height; }

    public CellType getCell(int x, int y) {
        if (isInBounds(x, y)) return cells[x][y];
        return null;
    }

    public void setCell(int x, int y, CellType cellType) {
        if (isInBounds(x, y)) cells[x][y] = cellType;
    }

    public boolean isInBounds(int x, int y) {
        return x >= 0 && x < width && y >= 0 && y < height;
    }
}