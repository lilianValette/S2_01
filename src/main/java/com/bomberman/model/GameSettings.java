package com.bomberman.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

/**
 * Paramètres globaux du jeu (niveau IA, etc.).
 * Permet des bindings entre tous les contrôleurs JavaFX.
 */
public class GameSettings {
    // Valeurs possibles : 0=FACILE, 1=NORMAL, 2=DIFFICILE, 3=EXPERT
    public static final String[] AI_LEVELS = {"FACILE", "NORMAL", "DIFFICILE"};
    private static final IntegerProperty aiLevelIndex = new SimpleIntegerProperty(0);

    public static IntegerProperty aiLevelIndexProperty() { return aiLevelIndex; }
    public static int getAiLevelIndex() { return aiLevelIndex.get(); }
    public static void setAiLevelIndex(int idx) { aiLevelIndex.set(idx); }

    public static AIDifficulty getSelectedAIDifficulty() {
        return AIDifficulty.valueOf(AI_LEVELS[getAiLevelIndex()]);
    }
}