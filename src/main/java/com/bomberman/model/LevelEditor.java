package com.bomberman.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Classe de base pour la gestion de l'édition de niveaux personnalisés.
 * Permet de créer, modifier et sauvegarder des niveaux Bomberman.
 */
public class LevelEditor {

    private int width;
    private int height;
    private List<List<CellType>> grid;

    public enum CellType {
        EMPTY,
        WALL,           // Mur indestructible
        BREAKABLE,      // Mur destructible
        PLAYER_SPAWN,
        AI_SPAWN,
        BONUS
        // Ajoute d'autres types si besoin...
    }

    public LevelEditor(int width, int height) {
        this.width = width;
        this.height = height;
        this.grid = new ArrayList<>();
        for (int y = 0; y < height; y++) {
            List<CellType> row = new ArrayList<>();
            for (int x = 0; x < width; x++) {
                row.add(CellType.EMPTY);
            }
            grid.add(row);
        }
    }

    public int getWidth() { return width; }
    public int getHeight() { return height; }

    public CellType getCell(int x, int y) {
        if (isInBounds(x, y)) {
            return grid.get(y).get(x);
        }
        return null;
    }

    public void setCell(int x, int y, CellType type) {
        if (isInBounds(x, y)) {
            grid.get(y).set(x, type);
        }
    }

    public void clear() {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                grid.get(y).set(x, CellType.EMPTY);
            }
        }
    }

    public List<List<CellType>> getGrid() {
        return grid;
    }

    private boolean isInBounds(int x, int y) {
        return x >= 0 && x < width && y >= 0 && y < height;
    }

    // Conversion vers int[][] compatible avec Level
    public int[][] toIntLayout() {
        int[][] layout = new int[height][width];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                layout[y][x] = switch (grid.get(y).get(x)) {
                    case WALL -> 1;
                    case BREAKABLE -> 2;
                    default -> 0;
                };
            }
        }
        return layout;
    }

    // Conversion depuis int[][] (Level -> LevelEditor)
    public void fromIntLayout(int[][] layout) {
        for (int y = 0; y < height && y < layout.length; y++) {
            for (int x = 0; x < width && x < layout[y].length; x++) {
                grid.get(y).set(x, switch (layout[y][x]) {
                    case 1 -> CellType.WALL;
                    case 2 -> CellType.BREAKABLE;
                    default -> CellType.EMPTY;
                });
            }
        }
    }

    // Optionnel : créer un Level à partir de l'éditeur
    public Level toLevel(String name, String groundImg, String wallIndImg, String wallDesImg) {
        return new Level(name, groundImg, wallIndImg, wallDesImg, toIntLayout());
    }

    // Optionnel : charger l'état de l'éditeur depuis un Level
    public void loadFromLevel(Level level) {
        fromIntLayout(level.getLayout());
        // Gère ici les images si besoin
    }
}