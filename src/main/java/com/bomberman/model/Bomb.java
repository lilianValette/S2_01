package com.bomberman.model;

public class Bomb {
    private int x, y;
    private int timer; // nombre de ticks avant explosion
    private int range; // rayon d’explosion

    public Bomb(int x, int y, int timer, int range) {
        this.x = x;
        this.y = y;
        this.timer = timer;
        this.range = range;
    }

    public int getX() { return x; }
    public int getY() { return y; }
    public int getTimer() { return timer; }
    public int getRange() { return range; }

    public void tick() { timer--; }
    public boolean isExploded() { return timer <= 0; }
}