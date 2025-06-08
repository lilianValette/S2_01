package com.bomberman.model;

/**
 * Représente un niveau (layout, images, nom) pour Bomberman.
 */
public class Level {
    private final String name;
    private final String groundImagePath;
    private final String wallIndestructibleImagePath;
    private final String wallDestructibleImagePath;
    private final int[][] layout; // 0: sol, 1: mur indestructible, 2: destructible

    public Level(String name, String groundImagePath, String wallIndestructibleImagePath, String wallDestructibleImagePath, int[][] layout) {
        this.name = name;
        this.groundImagePath = groundImagePath;
        this.wallIndestructibleImagePath = wallIndestructibleImagePath;
        this.wallDestructibleImagePath = wallDestructibleImagePath;
        this.layout = layout;
    }

    public String getName() { return name; }
    public String getGroundImagePath() { return groundImagePath; }
    public String getWallIndestructibleImagePath() { return wallIndestructibleImagePath; }
    public String getWallDestructibleImagePath() { return wallDestructibleImagePath; }
    public int[][] getLayout() { return layout; }

    /**
     * Renvoie les niveaux prédéfinis disponibles.
     */
    public static Level[] getPredefinedLevels() {
        return new Level[] {
                new Level("Classic",
                        "/images/elementsMap/herbe.png",
                        "/images/elementsMap/murIndestructible.png",
                        "/images/elementsMap/murDestructible.png",
                        new int[][] {
                                {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
                                {1,0,0,0,2,2,1,2,1,2,2,0,0,0,1},
                                {1,0,1,0,1,2,1,0,1,2,1,0,1,0,1},
                                {1,0,0,0,2,2,1,2,1,2,2,0,0,0,1},
                                {1,2,1,2,1,2,1,2,1,2,1,2,1,2,1},
                                {1,2,2,2,2,2,2,0,2,2,2,2,2,2,1},
                                {1,1,1,0,1,1,1,0,1,1,1,0,1,1,1},
                                {1,2,2,2,2,2,2,0,2,2,2,2,2,2,1},
                                {1,2,1,2,1,2,1,2,1,2,1,2,1,2,1},
                                {1,0,0,0,2,2,1,2,1,2,2,0,0,0,1},
                                {1,0,1,0,1,2,1,0,1,2,1,0,1,0,1},
                                {1,0,0,0,2,2,1,2,1,2,2,0,0,0,1},
                                {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1}
                        }
                ),
                new Level("Snow",
                        "/images/elementsMap/fondNeige.png",
                        "/images/elementsMap/blocGlasse.png",
                        "/images/elementsMap/BonhommeNeige.png",
                        new int[][] {
                                {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
                                {1,0,0,0,2,2,2,2,2,2,2,0,0,0,1},
                                {1,0,1,0,1,1,1,1,1,1,1,0,1,0,1},
                                {1,0,0,0,2,2,2,2,2,2,2,0,0,0,1},
                                {1,2,1,2,1,2,1,2,1,2,1,2,1,2,1},
                                {1,2,1,2,1,2,1,2,1,2,1,2,1,2,1},
                                {1,2,1,2,1,2,1,2,1,2,1,2,1,2,1},
                                {1,2,1,2,1,2,1,2,1,2,1,2,1,2,1},
                                {1,0,0,0,2,2,2,2,2,2,2,0,0,0,1},
                                {1,0,1,0,1,1,1,1,1,1,1,0,1,0,1},
                                {1,0,0,0,2,2,2,2,2,2,2,0,0,0,1},
                                {1,0,1,0,1,1,1,1,1,1,1,0,1,0,1},
                                {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1}
                        }
                ),
                new Level("Forest",
                        "/images/elementsMap/fondFeuilles.png",
                        "/images/elementsMap/soucheArbre.png",
                        "/images/elementsMap/buisson.png",
                        new int[][] {
                                {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
                                {1,0,0,2,2,2,2,2,2,2,2,2,0,0,1},
                                {1,0,0,0,1,1,1,1,1,1,1,0,0,0,1},
                                {1,2,0,0,2,2,2,2,2,2,2,0,0,2,1},
                                {1,2,1,2,0,0,1,1,1,0,0,2,1,2,1},
                                {1,2,1,2,0,0,2,2,2,0,0,2,1,2,1},
                                {1,2,1,2,1,2,1,0,1,2,1,2,1,2,1},
                                {1,2,1,2,1,2,0,0,0,2,1,2,1,2,1},
                                {1,2,1,2,1,2,1,0,1,2,1,2,1,2,1},
                                {1,2,1,2,0,0,2,2,2,0,0,2,1,2,1},
                                {1,0,0,2,0,0,1,1,1,0,0,2,0,0,1},
                                {1,0,0,2,2,2,2,2,2,2,2,2,0,0,1},
                                {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1}
                        }
                ),
                new Level("Volcan",
                        "/images/elementsMap/solVolcanique.png",
                        "/images/elementsMap/lave.png",
                        "/images/elementsMap/murDestructible.png",
                        new int[][] {
                                {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
                                {1,0,0,0,2,2,2,0,2,2,2,0,0,0,1},
                                {1,0,1,0,1,2,1,0,1,2,1,0,1,0,1},
                                {1,2,2,2,2,0,2,2,2,0,2,2,2,2,1},
                                {1,2,1,2,1,2,1,2,1,2,1,2,1,2,1},
                                {1,0,0,0,2,2,2,0,2,2,2,0,0,0,1},
                                {1,2,2,2,1,1,1,0,1,1,1,2,2,2,1},
                                {1,0,0,0,2,2,2,0,2,2,2,0,0,0,1},
                                {1,2,1,2,1,2,1,2,1,2,1,2,1,2,1},
                                {1,2,2,2,2,0,2,2,2,0,2,2,2,2,1},
                                {1,0,1,0,1,2,1,0,1,2,1,0,1,0,1},
                                {1,0,0,0,2,2,2,0,2,2,2,0,0,0,1},
                                {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1}
                        }
                ),
                new Level("Beach",
                        "/images/elementsMap/fondSable.png",
                        "/images/elementsMap/water.png",
                        "/images/elementsMap/sandCastle.png",
                        new int[][] {
                                {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
                                {1,0,0,0,1,2,2,2,2,2,1,0,0,0,1},
                                {1,0,1,0,1,0,1,2,1,0,1,0,1,0,1},
                                {1,0,0,0,1,2,2,2,2,2,1,0,0,0,1},
                                {1,2,1,2,1,0,1,2,1,0,1,2,1,2,1},
                                {1,2,2,2,2,2,2,0,2,2,2,2,2,2,1},
                                {1,1,1,0,1,1,1,0,1,1,1,0,1,1,1},
                                {1,2,2,2,2,2,2,0,2,2,2,2,2,2,1},
                                {1,2,1,2,1,0,1,2,1,0,1,2,1,2,1},
                                {1,0,0,0,1,2,2,2,2,2,1,0,0,0,1},
                                {1,0,1,0,1,0,1,2,1,0,1,0,1,0,1},
                                {1,0,0,0,1,2,2,2,2,2,1,0,0,0,1},
                                {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1}
                        }
                )
        };
    }
}