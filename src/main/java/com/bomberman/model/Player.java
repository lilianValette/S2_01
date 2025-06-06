package com.bomberman.model;

public class Player {
    private int id;
    private int x;
    private int y;
    private boolean alive;
    private int lives;
    private boolean isHuman;

    public Player(int id, int startX, int startY, boolean isHuman) {
        this.id = id;
        this.x = startX;
        this.y = startY;
        this.lives = 3;
        this.alive = true;
        this.isHuman = isHuman;
    }

    public int getId() { return id; }
    public int getX() { return x; }
    public int getY() { return y; }
    public boolean isAlive() { return alive; }
    public int getLives() { return lives; }
    public boolean isHuman() { return isHuman; }

    public void kill() {
        this.alive = false;
    }

    public void takeDamage() {
        if (lives > 0) {
            lives--;
            if (lives == 0) {
                alive = false;
            }
        }
    }

    public void move(int dx, int dy, Grid grid) {
        if (!alive) return;
        int newX = x + dx;
        int newY = y + dy;
        if (grid.isInBounds(newX, newY) && grid.getCell(newX, newY) == Grid.CellType.EMPTY) {
            this.x = newX;
            this.y = newY;
        }
    }

    public void moveUp(Grid grid)    { move(0, -1, grid); }
    public void moveDown(Grid grid)  { move(0,  1, grid); }
    public void moveLeft(Grid grid)  { move(-1, 0, grid); }
    public void moveRight(Grid grid) { move(1,  0, grid); }
}