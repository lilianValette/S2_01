package com.bomberman.model;
import java.io.*;
import java.nio.file.*;
import java.util.*;

/**
 * Gestionnaire des comptes utilisateurs avec sauvegarde/chargement depuis fichier.
 */
public class UserManager {
    private static final String USERS_FILE = "users.txt";
    private static UserManager instance;
    private Map<String, User> users;
    private User currentUser;

    private UserManager() {
        users = new HashMap<>();
        loadUsers();
    }

    public static UserManager getInstance() {
        if (instance == null) {
            instance = new UserManager();
        }
        return instance;
    }

    /**
     * Charge les utilisateurs depuis le fichier.
     */
    private void loadUsers() {
        try {
            Path path = Paths.get(USERS_FILE);
            if (Files.exists(path)) {
                List<String> lines = Files.readAllLines(path);
                for (String line : lines) {
                    if (!line.trim().isEmpty()) {
                        User user = User.fromString(line);
                        if (user != null) {
                            users.put(user.getUsername(), user);
                        }
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Erreur lors du chargement des utilisateurs : " + e.getMessage());
        }
    }

    /**
     * Sauvegarde les utilisateurs dans le fichier.
     */
    private void saveUsers() {
        try {
            List<String> lines = new ArrayList<>();
            for (User user : users.values()) {
                lines.add(user.toString());
            }
            Files.write(Paths.get(USERS_FILE), lines);
        } catch (IOException e) {
            System.err.println("Erreur lors de la sauvegarde des utilisateurs : " + e.getMessage());
        }
    }

    /**
     * Crée un nouveau compte utilisateur.
     * @param username nom d'utilisateur
     * @param password mot de passe
     * @return true si le compte a été créé avec succès, false si le nom d'utilisateur existe déjà
     */
    public boolean createAccount(String username, String password) {
        if (username == null || username.trim().isEmpty() ||
                password == null || password.trim().isEmpty()) {
            return false;
        }

        if (users.containsKey(username)) {
            return false; // Utilisateur déjà existant
        }

        User newUser = new User(username, password);
        users.put(username, newUser);
        saveUsers();
        return true;
    }

    /**
     * Authentifie un utilisateur.
     * @param username nom d'utilisateur
     * @param password mot de passe
     * @return true si l'authentification réussit
     */
    public boolean login(String username, String password) {
        User user = users.get(username);
        if (user != null && user.getPassword().equals(password)) {
            currentUser = user;
            return true;
        }
        return false;
    }

    /**
     * Déconnecte l'utilisateur actuellement connecté.
     */
    public void logout() {
        currentUser = null;
    }

    /**
     * @return l'utilisateur actuellement connecté, ou null si aucun
     */
    public User getCurrentUser() {
        return currentUser;
    }

    /**
     * @return true si un utilisateur est connecté
     */
    public boolean isLoggedIn() {
        return currentUser != null;
    }

    /**
     * Vérifie si un nom d'utilisateur existe déjà.
     */
    public boolean userExists(String username) {
        return users.containsKey(username);
    }

    /**
     * Met à jour les statistiques de l'utilisateur actuel et sauvegarde.
     */
    public void updateCurrentUserStats(boolean won, int score) {
        if (currentUser != null) {
            currentUser.incrementGamesPlayed();
            if (won) {
                currentUser.incrementGamesWon();
            }
            currentUser.addScore(score);
            saveUsers();
        }
    }
}
