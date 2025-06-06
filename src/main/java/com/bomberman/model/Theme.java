package com.bomberman.model;

public class Theme {
    private final String name;
    private final String solImagePath;
    private final String murImagePath;
    private final String destructibleImagePath;
    private final int[][] previewLayout;

    public Theme(String name, String solImagePath, String murImagePath, String destructibleImagePath, int[][] previewLayout) {
        this.name = name;
        this.solImagePath = solImagePath;
        this.murImagePath = murImagePath;
        this.destructibleImagePath = destructibleImagePath;
        this.previewLayout = previewLayout;
    }

    public String getName() { return name; }
    public String getSolImagePath() { return solImagePath; }
    public String getMurImagePath() { return murImagePath; }
    public String getDestructibleImagePath() { return destructibleImagePath; }
    public int[][] getPreviewLayout() { return previewLayout; }

    // Exemples de thèmes prédéfinis, à compléter selon tes assets
    public static Theme[] getPredefinedThemes() {
        return new Theme[] {
                new Theme("Test",
                        "/images/elementsMap/herbe.png",
                        "/images/elementsMap/murIndestructible.png",
                        "/images/elementsMap/murDestructible.png",
                        new int[][] {
                                {1,0,0,2,0,1},
                                {0,2,1,0,2,0},
                                {0,0,0,0,0,0},
                                {2,1,0,2,1,2},
                                {1,0,2,0,0,1}
                        }
                )
                /*,
                new Theme("Forest",
                        "/images/elementsMap/forest_sol.png",
                        "/images/elementsMap/forest_mur.png",
                        "/images/elementsMap/forest_bloc.png",
                        new int[][] {
                                {1,0,2,0,2,1},
                                {0,0,0,2,0,0},
                                {2,0,1,0,1,2},
                                {0,2,0,0,2,0},
                                {1,0,2,1,0,1}
                        }
                )*/
        };
    }
}