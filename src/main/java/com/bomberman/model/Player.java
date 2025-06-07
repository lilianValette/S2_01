package com.bomberman.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Représente un joueur (humain ou IA) dans le jeu Bomberman.
 */
public class Player {
    private int id;
    private int x;
    private int y;
    private boolean alive;
    private int lives;
    private int bombRange = 1;   // portée de base (= 1 case)
    private int maxBombs  = 1;   // nombre de bombes simultanées autorisées
    private List<ActiveBonus> activeBonuses = new ArrayList<>();
    private boolean isHuman;

    /**
     * Crée un joueur.
     * @param id identifiant du joueur (1, 2, ...)
     * @param startX position X initiale
     * @param startY position Y initiale
     * @param isHuman true si humain, false si IA
     */
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

    /**
     * Renvoie true si ce joueur est une IA.
     */
    public boolean isAI() { return !isHuman; }

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

    public int getBombRange() { return bombRange; }
    public void setBombRange(int bombRange) { this.bombRange = bombRange; }
    public int getMaxBombs() { return maxBombs; }
    public void setMaxBombs(int maxBombs) { this.maxBombs = maxBombs; }
    public List<ActiveBonus> getActiveBonuses() { return activeBonuses; }

    public Bomb dropBomb(int timer, List<Bomb> activeBombs) {
        if (activeBombs.size() < maxBombs) {
            return new Bomb(this.x, this.y, timer, this.bombRange);
        }
        return null;
    }

    /**
     * Met à jour la durée des bonus actifs (appelé chaque tick, 0.5 s).
     */
    public void updateActiveBonuses() {
        double tickDuration = 0.5; // chaque tick correspond à 0.5 seconde
        Iterator<ActiveBonus> it = activeBonuses.iterator();
        while (it.hasNext()) {
            ActiveBonus ab = it.next();
            ab.tick(tickDuration);
            if (ab.isExpired()) {
                switch (ab.getType()) {
                    case FLAME:
                        bombRange -= ab.getExtraValue();
                        break;
                }
                it.remove();
            }
        }
    }

    /**
     * Ajoute un bonus FLAME temporaire de durée donnée (en secondes).
     */
    public void addFlameBonusTemp(int extraRange, double durationSeconds) {
        bombRange += extraRange;
        activeBonuses.add(new ActiveBonus(ActiveBonus.Type.FLAME, extraRange, durationSeconds));
    }
}