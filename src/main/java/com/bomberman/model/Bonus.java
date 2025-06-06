package com.bomberman.model;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import java.io.InputStream;

/**
 * Classe abstraite représentant un bonus sur la map.
 * Chaque bonus sait :
 *   1) où il se trouve (en coordonnées « tile »),
 *   2) s’il a déjà été ramassé (collected),
 *   3) comment se dessiner (une image JavaFX),
 *   4) comment s’appliquer à un Player (méthode abstraite applyTo).
 */
public abstract class Bonus {
    protected int x;              // X en cases (tiles)
    protected int y;              // Y en cases (tiles)
    protected boolean collected;  // true si le bonus a déjà été ramassé
    protected Image sprite;       // image du bonus

    /**
     * @param x         coordonnée X en cases
     * @param y         coordonnée Y en cases
     * @param resource  chemin dans le classpath vers l’image du bonus
     *                  (par exemple "/com/bomberman/images/flame_bonus.png")
     */
    public Bonus(int x, int y, String resource) {
        this.x = x;
        this.y = y;
        this.collected = false;
        loadSprite(resource);
    }

    /**
     * Charge l’image depuis le classpath.
     */
    private void loadSprite(String resourcePath) {
        try (InputStream is = getClass().getResourceAsStream(resourcePath)) {
            if (is == null) {
                System.err.println("Bonus : impossible de trouver la ressource " + resourcePath);
                sprite = null;
            } else {
                sprite = new Image(is);
            }
        } catch (Exception e) {
            System.err.println("Bonus : erreur en chargeant l’image " + resourcePath);
            e.printStackTrace();
            sprite = null;
        }
    }

    /** @return true si le bonus a déjà été collecté par le joueur */
    public boolean isCollected() {
        return collected;
    }

    /** @return la coordonnée X (en tiles) du bonus */
    public int getX() {
        return x;
    }

    /** @return la coordonnée Y (en tiles) du bonus */
    public int getY() {
        return y;
    }

    /**
     * Méthode à surcharger : définit l’effet que ce bonus applique au Player.
     * Par exemple : augmenter la portée des bombes, la vitesse, etc.
     *
     * @param player le joueur qui ramasse le bonus
     */
    public abstract void applyTo(Player player);

    /**
     * Dessine ce bonus à l’écran, en pixels, sur un GraphicsContext JavaFX.
     *
     * @param gc        GraphicsContext (par ex. celui d’un Canvas)
     * @param tileSize  taille, en pixels, d’une case (tile) du jeu
     */
    public void draw(GraphicsContext gc, int tileSize) {
        if (!collected && sprite != null) {
            double pixelX = x * tileSize;
            double pixelY = y * tileSize;
            gc.drawImage(sprite, pixelX, pixelY, tileSize, tileSize);
        }
    }
}
