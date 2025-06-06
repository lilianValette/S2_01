package com.bomberman.model;

/**
 * Bonus « Flamme » : lorsqu’il est ramassé, il augmente la portée des bombes du joueur.
 */
public class FlameBonus extends Bonus {
    private int extraRange;

    /**
     * @param x          coordonnée X du bonus (en cases)
     * @param y          coordonnée Y du bonus (en cases)
     * @param extraRange nombre de cases supplémentaires à ajouter à la portée des bombes
     */
    public FlameBonus(int x, int y, int extraRange) {
        // On suppose que l’image flame_bonus.png est dans /com/bomberman/images/
        super(x, y, "/images/items/flame_bonus.png");
        this.extraRange = extraRange;
    }

    @Override
    public void applyTo(Player player) {
        if (!collected) {
            // NOTE : il faut que votre Player gère un attribut bombRange et
            // fournisse une méthode pour l’incrémenter ou le setter.
            // Ici, on suppose qu’il possède :
            //    public int getBombRange();
            //    public void setBombRange(int nouvelleRange);
            // Ou, préférablement :
            //    public void increaseBombRange(int amount);
            //
            // Si vous n’avez pas encore ces méthodes, il faudra les ajouter dans Player.

            // Exemple avec setBombRange :
            int currentRange = player.getBombRange();
            player.setBombRange(currentRange + extraRange);

            // OU, si vous avez choisi d’implémenter increaseBombRange :
            // player.increaseBombRange(extraRange);

            collected = true;
            // (Éventuellement déclencher un son ou une animation de ramassage ici)
        }
    }
}
