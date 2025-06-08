
package com.bomberman.model;

public class User {
    private String username;
    private String password;
    private int gamesPlayed;
    private int gamesWon;
    private int totalScore;
    private Character selectedCharacter;
    private String character;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.gamesPlayed = 0;
        this.gamesWon = 0;
        this.totalScore = 0;
        this.selectedCharacter = Character.BOMBERMAN_1; // Personnage par défaut
    }

    // Constructeur complet pour charger depuis fichier
    public User(String username, String password, int gamesPlayed, int gamesWon, int totalScore, Character selectedCharacter) {
        this.username = username;
        this.password = password;
        this.gamesPlayed = gamesPlayed;
        this.gamesWon = gamesWon;
        this.totalScore = totalScore;
        this.selectedCharacter = selectedCharacter != null ? selectedCharacter : Character.BOMBERMAN_1;
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

    public Character getSelectedCharacter() {
        return selectedCharacter;
    }

    public void setSelectedCharacter(Character selectedCharacter) {
        this.selectedCharacter = selectedCharacter;
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
        return String.format("%s,%s,%d,%d,%d,%s",
                username, password, gamesPlayed, gamesWon, totalScore,
                selectedCharacter.name());
    }

    public static User fromString(String line) {
        String[] parts = line.split(",");
        if (parts.length >= 5) {
            Character character = Character.BOMBERMAN_1; // Par défaut
            if (parts.length >= 6) {
                character = Character.fromString(parts[5]);
            }
            return new User(
                    parts[0],
                    parts[1],
                    Integer.parseInt(parts[2]),
                    Integer.parseInt(parts[3]),
                    Integer.parseInt(parts[4]),
                    character
            );
        }
        return null;
    }
}