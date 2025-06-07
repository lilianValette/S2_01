package com.bomberman.model;

public class Level {
    private final String name;
    private final String solImagePath;
    private final String murImagePath;
    private final String destructibleImagePath;
    private final int[][] layout; // Nommé "layout" car c'est maintenant la vraie map

    public Level(String name, String solImagePath, String murImagePath, String destructibleImagePath, int[][] layout) {
        this.name = name;
        this.solImagePath = solImagePath;
        this.murImagePath = murImagePath;
        this.destructibleImagePath = destructibleImagePath;
        this.layout = layout;
    }

    public String getName() { return name; }
    public String getSolImagePath() { return solImagePath; }
    public String getMurImagePath() { return murImagePath; }
    public String getDestructibleImagePath() { return destructibleImagePath; }
    public int[][] getLayout() { return layout; }

    public static Level[] getPredefinedThemes() {
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