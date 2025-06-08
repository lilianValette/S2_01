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

    // Placeholder pour sauvegarde/chargement (à implémenter selon le format choisi)
    public void saveLevel(String filename) {
        // TODO: implémenter la sauvegarde de la grille dans un fichier
    }

    public void loadLevel(String filename) {
        // TODO: implémenter le chargement d'une grille depuis un fichier
    }
}