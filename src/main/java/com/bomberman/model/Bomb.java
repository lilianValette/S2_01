package com.bomberman.model;

/**
 * Représente une bombe posée sur la grille de Bomberman.
 */
public class Bomb {
    public static final int DEFAULT_TIMER = 6; // nombre de ticks avant explosion (~2s à 0.5s/tick)

    private final int x;
    private final int y;
    private int timer; // nombre de ticks avant explosion
    private final int range; // rayon d’explosion
    private final Player owner; // joueur ayant posé la bombe

    /**
     * Crée une bombe avec un propriétaire explicite.
     * @param x position X
     * @param y position Y
     * @param timer durée en ticks avant explosion
     * @param range portée de l’explosion
     * @param owner joueur ayant posé la bombe
     */
    public Bomb(int x, int y, int timer, int range, Player owner) {
        this.x = x;
        this.y = y;
        this.timer = timer;
        this.range = range;
        this.owner = owner;
    }

    /**
     * Constructeur rétrocompatible (sans owner).
     * @param x position X
     * @param y position Y
     * @param timer durée en ticks avant explosion
     * @param range portée de l’explosion
     */
    public Bomb(int x, int y, int timer, int range) {
        this(x, y, timer, range, null);
    }

    /** @return position X de la bombe */
    public int getX() { return x; }

    /** @return position Y de la bombe */
    public int getY() { return y; }

    /** @return nombre de ticks restants avant explosion */
    public int getTimer() { return timer; }

    /** @return portée de l’explosion */
    public int getRange() { return range; }

    /** @return joueur ayant posé la bombe, ou null si inconnu */
    public Player getOwner() { return owner; }

    /** Fait avancer le timer d’un tick. */
    public void tick() { timer--; }

    /** @return true si la bombe a explosé */
    public boolean isExploded() { return timer <= 0; }
}