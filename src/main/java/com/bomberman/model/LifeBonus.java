package com.bomberman.model;

import javafx.scene.image.Image;

public class LifeBonus extends Bonus {

    public LifeBonus(int x, int y) {
        super(x, y, "/images/items/life_bonus.png");
    }

    @Override
    public void applyTo(Player player) {
        if (player.getLives() <3 ) {
            player.addLife();
        }
        this.setCollected(true);
    }
}