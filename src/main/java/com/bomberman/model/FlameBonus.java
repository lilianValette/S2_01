package com.bomberman.model;

public class FlameBonus extends Bonus {
    private final int extraRange;
    private final double durationSeconds = 10.0; // dure 10 secondes

    public FlameBonus(int x, int y, int extraRange) {
        super(x, y, "/images/items/flame_bonus.png");
        this.extraRange = extraRange;
    }

    @Override
    public void applyTo(Player player) {
        if (!collected) {
            player.addFlameBonusTemp(extraRange, durationSeconds);
            collected = true;
        }
    }
}