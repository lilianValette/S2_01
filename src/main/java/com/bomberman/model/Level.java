package com.bomberman.model;

import java.io.*;
import java.nio.file.*;
import java.util.*;

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

    // --- Chargement depuis un fichier .level ---
    public static Level fromFile(Path file) throws IOException {
        List<String> lines = Files.readAllLines(file);
        String name = "", ground = "", ind = "", des = "";
        int layoutStart = -1;
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i).trim();
            if (line.startsWith("name:")) name = line.substring(5).trim();
            else if (line.startsWith("groundImage:")) ground = line.substring(12).trim();
            else if (line.startsWith("wallIndestructibleImage:")) ind = line.substring(25).trim();
            else if (line.startsWith("wallDestructibleImage:")) des = line.substring(22).trim();
            else if (line.startsWith("layout:")) { layoutStart = i + 1; break; }
        }
        if (layoutStart < 0) throw new IOException("Fichier de niveau invalide (pas de layout)");
        int rows = lines.size() - layoutStart;
        int cols = lines.get(layoutStart).trim().length();
        int[][] layout = new int[rows][cols];
        for (int r = 0; r < rows; r++) {
            String l = lines.get(layoutStart + r).trim();
            for (int c = 0; c < cols; c++) {
                layout[r][c] = l.charAt(c) - '0';
            }
        }
        return new Level(name, ground, ind, des, layout);
    }

    // --- Sauvegarde dans un fichier .level ---
    public void saveToFile(Path file) throws IOException {
        try (BufferedWriter w = Files.newBufferedWriter(file)) {
            w.write("name: " + name + "\n");
            w.write("groundImage: " + groundImagePath + "\n");
            w.write("wallIndestructibleImage: " + wallIndestructibleImagePath + "\n");
            w.write("wallDestructibleImage: " + wallDestructibleImagePath + "\n");
            w.write("layout:\n");
            for (int[] row : layout) {
                for (int cell : row) w.write(Integer.toString(cell));
                w.write("\n");
            }
        }
    }

    // --- Liste tous les niveaux d'un dossier ---
    public static List<Level> loadLevelsFromDirectory(Path dir) throws IOException {
        List<Level> levels = new ArrayList<>();
        if (!Files.exists(dir)) return levels;
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir, "*.level")) {
            for (Path f : stream) {
                try { levels.add(Level.fromFile(f)); }
                catch (Exception e) { System.err.println("Erreur chargement niveau: " + f + " : " + e.getMessage()); }
            }
        }
        return levels;
    }
}