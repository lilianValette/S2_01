package com.bomberman.model;

/**
 * Représente un bonus appliqué à un joueur,
 * avec son type, sa valeur (extraRange) et sa durée restante en secondes.
 */
public class ActiveBonus {
    public enum Type { FLAME, JACKET, LIFE }

    private final Type type;
    private final int extraValue;
    private double secondsRemaining;  // durée restante en secondes

    /**
     * @param type           type de bonus (ici FLAME)
     * @param extraValue     ex. +1 case de portée
     * @param durationSeconds durée du bonus en secondes
     */
    public ActiveBonus(Type type, int extraValue, double durationSeconds) {
        this.type = type;
        this.extraValue = extraValue;
        this.secondsRemaining = durationSeconds;
    }

    public Type getType() {
        return type;
    }

    public int getExtraValue() {
        return extraValue;
    }

    /**
     * Retourne le nombre de secondes entières restantes (arrondi à l'entier supérieur).
     */
    public int getSecondsRemaining() {
        return (int) Math.ceil(secondsRemaining);
    }

    /**
     * Doit être appelé à chaque tick (0,5 s) pour décrémenter la durée réelle.
     */
    public void tick(double tickDuration) {
        secondsRemaining -= tickDuration;
    }

    public boolean isExpired() {
        return secondsRemaining <= 0;
    }
}
