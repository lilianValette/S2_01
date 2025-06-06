package com.bomberman.model;

public class Player {
    private int id;
    private int x;
    private int y;
    private boolean alive;
    private int lives;    private int bombRange = 1;   // portée de base (= 1 case)
    private int maxBombs  = 1;   // nombre de bombes simultanées autorisées

    public int getBombRange() {
        return bombRange;
    }

    public void setBombRange(int bombRange) {
        this.bombRange = bombRange;
    }

    // Si vous préférez encapsuler mieux :
    // public void increaseBombRange(int amount) {
    //     bombRange += amount;
    //     // éventuellement limiter à une valeur max :
    //     // bombRange = Math.min(bombRange, MAX_RANGE);
    // }

    public int getMaxBombs() {
        return maxBombs;
    }

    public void setMaxBombs(int maxBombs) {
        this.maxBombs = maxBombs;
    }
    public Player(int id, int startX, int startY) {
        this.id = id;
        this.x = startX;
        this.y = startY;
        this.lives = 3;
        this.alive = true;
    }

    public int getId() { return id; }
    public int getX() { return x; }
    public int getY() { return y; }
    public boolean isAlive() { return alive; }
    public int getLives() { return lives; }

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
        if (!alive) return; // Empêche le déplacement si mort
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