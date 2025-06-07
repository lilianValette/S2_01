package com.bomberman.model;

public class User {
    private String username;
    private String password;
    private int gamesPlayed;
    private int gamesWon;
    private int totalScore;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.gamesPlayed = 0;
        this.gamesWon = 0;
        this.totalScore = 0;
    }

    // Constructeur complet pour charger depuis fichier
    public User(String username, String password, int gamesPlayed, int gamesWon, int totalScore) {
        this.username = username;
        this.password = password;
        this.gamesPlayed = gamesPlayed;
        this.gamesWon = gamesWon;
        this.totalScore = totalScore;
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

    public int getTotalScore() {
        return totalScore;
    }

    public void incrementGamesPlayed() {
        this.gamesPlayed++;
    }

    public void incrementGamesWon() {
        this.gamesWon++;
    }

    public void addScore(int score) {
        this.totalScore += score;
    }

    public double getWinRate() {
        if (gamesPlayed == 0) return 0.0;
        return (double) gamesWon / gamesPlayed * 100.0;
    }

    @Override
    public String toString() {
        return String.format("%s,%s,%d,%d,%d",
                username, password, gamesPlayed, gamesWon, totalScore);
    }

    public static User fromString(String line) {
        String[] parts = line.split(",");
        if (parts.length == 5) {
            return new User(
                    parts[0],
                    parts[1],
                    Integer.parseInt(parts[2]),
                    Integer.parseInt(parts[3]),
                    Integer.parseInt(parts[4])
            );
        }
        return null;
    }
}
