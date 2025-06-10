package com.bomberman.model;

public class JacketBonus extends Bonus {
    private final double durationSeconds = 20.0;

    public JacketBonus(int x, int y) {
        super(x, y, "/images/items/jacket_bonus.png"); // mets ici ton image
    }

    @Override
    public void applyTo(Player player) {
        if (!collected) {
            player.addJacketBonusTemp(durationSeconds);
            collected = true;
        }
    }
}