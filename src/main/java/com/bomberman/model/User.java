package com.bomberman.model;

public class User {
    private String username;
    private String password;
    private int gamesPlayed;
    private int gamesWon;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.gamesPlayed = 0;
        this.gamesWon = 0;
    }

    // Constructeur complet pour charger depuis fichier - MODIFIÉ
    public User(String username, String password, int gamesPlayed, int gamesWon) {
        this.username = username;
        this.password = password;
        this.gamesPlayed = gamesPlayed;
        this.gamesWon = gamesWon;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public int getGamesPlayed() {
        return gamesPlayed;
    }

    public int getGamesWon() {
        return gamesWon;
    }

    public void incrementGamesPlayed() {
        this.gamesPlayed++;
    }

    public void incrementGamesWon() {
        this.gamesWon++;
    }


    public double getWinRate() {
        if (gamesPlayed == 0) return 0.0;
        return (double) gamesWon / gamesPlayed * 100.0;
    }

    @Override
    public String toString() {
        return String.format("%s,%s,%d,%d",
                username, password, gamesPlayed, gamesWon);
    }

    public static User fromString(String line) {
        String[] parts = line.split(",");
        if (parts.length == 4) { // MODIFIÉ : 4 au lieu de 5
            return new User(
                    parts[0],
                    parts[1],
                    Integer.parseInt(parts[2]),
                    Integer.parseInt(parts[3])
            );
        }
        return null;
    }
}