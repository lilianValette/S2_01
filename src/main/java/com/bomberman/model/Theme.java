package com.bomberman.model;

public class Theme {
    private final String name;
    private final String solImagePath;
    private final String murImagePath;
    private final String destructibleImagePath;
    private final int[][] layout; // Nommé "layout" car c'est maintenant la vraie map

    public Theme(String name, String solImagePath, String murImagePath, String destructibleImagePath, int[][] layout) {
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

    public static Theme[] getPredefinedThemes() {
        return new Theme[] {
                new Theme("Test1",
                        "/images/elementsMap/herbe.png",
                        "/images/elementsMap/murIndestructible.png",
                        "/images/elementsMap/murDestructible.png",
                        new int[][] {
                                {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
                                {1,0,0,0,2,0,2,0,2,0,2,0,2,0,1},
                                {1,2,1,0,1,0,1,0,1,0,1,0,1,2,1},
                                {1,0,0,2,0,2,0,2,0,2,0,2,0,0,1},
                                {1,2,1,0,1,0,1,0,1,0,1,0,1,2,1},
                                {1,0,2,0,2,0,2,0,2,0,2,0,2,0,1},
                                {1,1,1,0,1,1,0,1,1,1,0,1,1,1,1},
                                {1,0,2,0,2,0,2,0,2,0,2,0,2,0,1},
                                {1,2,1,0,1,0,1,0,1,0,1,0,1,2,1},
                                {1,0,0,2,0,2,0,2,0,2,0,2,0,0,1},
                                {1,2,1,0,1,0,1,0,1,0,1,0,1,2,1},
                                {1,0,2,0,2,0,2,0,2,0,2,0,0,0,1},
                                {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1}
                        }
                ),
                new Theme("Test2",
                        "/images/elementsMap/herbe.png",
                        "/images/elementsMap/murIndestructible.png",
                        "/images/elementsMap/murDestructible.png",
                        new int[][] {
                                {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
                                {1,0,2,0,0,2,0,2,0,2,0,2,0,2,1},
                                {1,0,1,0,1,0,1,0,1,0,1,0,1,0,1},
                                {1,2,0,2,0,2,0,2,0,2,0,2,0,2,1},
                                {1,0,1,0,1,0,1,0,1,0,1,0,1,0,1},
                                {1,2,0,2,0,2,0,2,0,2,0,2,0,2,1},
                                {1,0,1,0,1,0,1,0,1,0,1,0,1,0,1},
                                {1,2,0,2,0,2,0,2,0,2,0,2,0,2,1},
                                {1,1,1,1,0,1,1,1,0,1,1,1,1,1,1},
                                {1,2,0,2,0,2,0,2,0,2,0,2,0,2,1},
                                {1,0,1,0,1,0,1,0,1,0,1,0,1,0,1},
                                {1,2,0,2,0,2,0,2,0,2,0,2,0,2,1},
                                {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1}
                        }
                )
        };
    }
}