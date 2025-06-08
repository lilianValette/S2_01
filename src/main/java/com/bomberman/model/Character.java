package com.bomberman.model;

/**
 * Énumération des personnages disponibles dans le jeu.
 * Chaque personnage a des images pour différentes orientations.
 */
public enum Character {
    BOMBERMAN_1("Bomberman Classic",
            "/images/Player/PBlanc/PBlanc_face.png",
            "/images/Player/PBlanc/PBlanc_dos.png",
            "/images/Player/PBlanc/PBlanc_gauche.png",
            "/images/Player/PBlanc/PBlanc_droite.png",
            "/images/Player/PBlanc/PBlanc_icon.png"),

    BOMBERMAN_2("Bomberman Blue",
            "/images/Player/PBleuCiel/PBleuCiel_face.png",
            "/images/Player/PBleuCiel/PBleuCiel_dos.png",
            "/images/Player/PBleuCiel/PBleuCiel_gauche.png",
            "/images/Player/PBleuCiel/PBleuCiel_droite.png",
            "/images/Player/PBleuCiel/PBleuCiel_icon.png"),

    BOMBERMAN_3("Bomberman Pink",
            "/images/Player/PRose/PRose_face.png",
            "/images/Player/PRose/PRose_dos.png",
            "/images/Player/PRose/PRose_gauche.png",
            "/images/Player/PRose/PRose_droite.png",
            "/images/Player/PRose/PRose_icon.png"),

    BOMBERMAN_4("Bomberman Red",
            "/images/Player/PRouge/PRouge_face.png",
            "/images/Player/PRouge/PRouge_dos.png",
            "/images/Player/PRouge/PRouge_gauche.png",
            "/images/Player/PRouge/PRouge_droite.png",
            "/images/Player/PRouge/PRouge_icon.png");

    private final String displayName;
    private final String frontImagePath;
    private final String backImagePath;
    private final String leftImagePath;
    private final String rightImagePath;
    private final String iconImagePath;

    Character(String displayName, String frontImagePath, String backImagePath,
              String leftImagePath, String rightImagePath, String iconImagePath) {
        this.displayName = displayName;
        this.frontImagePath = frontImagePath;
        this.backImagePath = backImagePath;
        this.leftImagePath = leftImagePath;
        this.rightImagePath = rightImagePath;
        this.iconImagePath = iconImagePath;
    }

    public String getDisplayName() { return displayName; }
    public String getFrontImagePath() { return frontImagePath; }
    public String getBackImagePath() { return backImagePath; }
    public String getLeftImagePath() { return leftImagePath; }
    public String getRightImagePath() { return rightImagePath; }
    public String getIconImagePath() { return iconImagePath; }

    /**
     * Retourne le chemin de l'image selon la direction.
     * @param direction "front", "back", "left", "right", ou "icon"
     * @return le chemin de l'image correspondante
     */
    public String getImagePath(String direction) {
        switch (direction.toLowerCase()) {
            case "front": return frontImagePath;
            case "back": return backImagePath;
            case "left": return leftImagePath;
            case "right": return rightImagePath;
            case "icon": return iconImagePath;
            default: return frontImagePath; // Par défaut
        }
    }

    /**
     * Retourne un personnage à partir de son nom d'affichage.
     */
    public static Character fromDisplayName(String displayName) {
        for (Character character : values()) {
            if (character.getDisplayName().equals(displayName)) {
                return character;
            }
        }
        return BOMBERMAN_1; // Par défaut
    }

    /**
     * Retourne un personnage à partir de son nom d'enum.
     */
    public static Character fromString(String name) {
        try {
            return Character.valueOf(name);
        } catch (IllegalArgumentException e) {
            return BOMBERMAN_1; // Par défaut
        }
    }
}
