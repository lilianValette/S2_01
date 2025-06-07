package com.bomberman.controller;

import com.bomberman.model.User;
import com.bomberman.model.UserManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class AccountControler {
    @FXML private StackPane rootPane;
    @FXML private ImageView backgroundImage;

    // Conteneurs principaux
    @FXML private StackPane loginContainer;
    @FXML private StackPane createContainer;
    @FXML private StackPane profileContainer;

    // Boutons de navigation entre les vues
    @FXML private Button showCreateButton;
    @FXML private Button showLoginButton;

    // Éléments de connexion
    @FXML private TextField loginUsername;
    @FXML private PasswordField loginPassword;
    @FXML private Button loginButton;
    @FXML private Label loginMessage;

    // Éléments de création de compte
    @FXML private TextField createUsername;
    @FXML private PasswordField createPassword;
    @FXML private PasswordField confirmPassword;
    @FXML private Button createButton;
    @FXML private Label createMessage;

    // Éléments de profil
    @FXML private Label profileUsername;
    @FXML private Label profileGamesPlayed;
    @FXML private Label profileGamesWon;
    @FXML private Label profileWinRate;
    @FXML private Label profileTotalScore;
    @FXML private Button logoutButton;

    // Bouton de retour
    @FXML private Button backButton;

    private Stage stage;
    private UserManager userManager;

    public void setStage(Stage stage) {
        this.stage = stage;
        // Définir une taille fixe identique au menu
        stage.setWidth(800);
        stage.setHeight(600);
        stage.setMinWidth(800);
        stage.setMinHeight(600);
        stage.setMaxWidth(800);
        stage.setMaxHeight(600);
        stage.setResizable(false);
        stage.centerOnScreen();
    }

    @FXML
    public void initialize() {
        userManager = UserManager.getInstance();

        // Charger l'image de fond
        loadBackgroundImage();

        // Configuration des boutons
        loginButton.setOnAction(e -> handleLogin());
        createButton.setOnAction(e -> handleCreateAccount());
        logoutButton.setOnAction(e -> handleLogout());
        backButton.setOnAction(e -> returnToMenu());

        // Boutons de navigation
        showCreateButton.setOnAction(e -> showCreateView());
        showLoginButton.setOnAction(e -> showLoginView());

        // Mise à jour de l'affichage selon l'état de connexion
        updateUI();

        // Listeners pour validation en temps réel
        createUsername.textProperty().addListener((obs, oldVal, newVal) -> validateCreateForm());
        createPassword.textProperty().addListener((obs, oldVal, newVal) -> validateCreateForm());
        confirmPassword.textProperty().addListener((obs, oldVal, newVal) -> validateCreateForm());
    }

    private void loadBackgroundImage() {
        try {
            java.net.URL url = getClass().getResource("/images/menu/Bomber_fond.jpg");
            if (url != null) {
                backgroundImage.setImage(new Image(url.toExternalForm()));
                backgroundImage.setFitWidth(800);
                backgroundImage.setFitHeight(600);
                backgroundImage.setPreserveRatio(false);
            } else {
                System.err.println("Image de fond non trouvée : /images/menu/Bomber_fond.jpg");
            }
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement de l'image de fond : " + e.getMessage());
        }
    }

    private void showLoginView() {
        loginContainer.setVisible(true);
        createContainer.setVisible(false);
        profileContainer.setVisible(false);
        clearMessages();
        // Reset des champs de connexion
        loginUsername.clear();
        loginPassword.clear();
    }

    private void showCreateView() {
        loginContainer.setVisible(false);
        createContainer.setVisible(true);
        profileContainer.setVisible(false);
        clearMessages();
        // Reset des champs de création
        clearCreateForm();
    }

    private void showProfileView() {
        loginContainer.setVisible(false);
        createContainer.setVisible(false);
        profileContainer.setVisible(true);
        updateProfileInfo();
    }

    private void handleLogin() {
        String username = loginUsername.getText().trim();
        String password = loginPassword.getText();

        if (username.isEmpty() || password.isEmpty()) {
            showLoginMessage("Veuillez remplir tous les champs.", true);
            return;
        }

        if (userManager.login(username, password)) {
            showLoginMessage("Connexion réussie ! Bienvenue " + username, false);
            // Petit délai pour afficher le message puis basculer vers le profil
            javafx.application.Platform.runLater(() -> {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                updateUI();
            });
        } else {
            showLoginMessage("Nom d'utilisateur ou mot de passe incorrect.", true);
        }
    }

    private void handleCreateAccount() {
        String username = createUsername.getText().trim();
        String password = createPassword.getText();
        String confirm = confirmPassword.getText();

        if (username.isEmpty() || password.isEmpty() || confirm.isEmpty()) {
            showCreateMessage("Veuillez remplir tous les champs.", true);
            return;
        }

        if (username.length() < 3) {
            showCreateMessage("Le nom d'utilisateur doit contenir au moins 3 caractères.", true);
            return;
        }

        if (password.length() < 4) {
            showCreateMessage("Le mot de passe doit contenir au moins 4 caractères.", true);
            return;
        }

        if (!password.equals(confirm)) {
            showCreateMessage("Les mots de passe ne correspondent pas.", true);
            return;
        }

        if (userManager.createAccount(username, password)) {
            showCreateMessage("Compte créé avec succès ! Vous pouvez maintenant vous connecter.", false);
            // Basculer vers la vue de connexion après un court délai
            javafx.application.Platform.runLater(() -> {
                try {
                    Thread.sleep(1500);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                showLoginView();
            });
        } else {
            showCreateMessage("Ce nom d'utilisateur est déjà pris.", true);
        }
    }

    private void handleLogout() {
        userManager.logout();
        clearAllForms();
        updateUI();
        showLoginMessage("Vous avez été déconnecté.", false);
    }

    private void validateCreateForm() {
        String username = createUsername.getText().trim();
        String password = createPassword.getText();
        String confirm = confirmPassword.getText();

        createButton.setDisable(username.length() < 3 || password.length() < 4 ||
                !password.equals(confirm) || confirm.isEmpty());
    }

    private void updateUI() {
        boolean isLoggedIn = userManager.isLoggedIn();

        if (isLoggedIn) {
            showProfileView();
        } else {
            showLoginView();
        }
    }

    private void updateProfileInfo() {
        User user = userManager.getCurrentUser();
        if (user != null) {
            profileUsername.setText(user.getUsername());
            profileGamesPlayed.setText(String.valueOf(user.getGamesPlayed()));
            profileGamesWon.setText(String.valueOf(user.getGamesWon()));
            profileWinRate.setText(String.format("%.1f%%", user.getWinRate()));
            profileTotalScore.setText(String.valueOf(user.getTotalScore()));
        }
    }

    private void showLoginMessage(String message, boolean isError) {
        loginMessage.setText(message);
        loginMessage.setStyle(isError ? "-fx-text-fill: red;" : "-fx-text-fill: green;");
    }

    private void showCreateMessage(String message, boolean isError) {
        createMessage.setText(message);
        createMessage.setStyle(isError ? "-fx-text-fill: red;" : "-fx-text-fill: green;");
    }

    private void clearMessages() {
        loginMessage.setText("");
        createMessage.setText("");
    }

    private void clearCreateForm() {
        createUsername.clear();
        createPassword.clear();
        confirmPassword.clear();
        createMessage.setText("");
    }

    private void clearAllForms() {
        loginUsername.clear();
        loginPassword.clear();
        loginMessage.setText("");
        clearCreateForm();
    }

    private void returnToMenu() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/bomberman/view/menu.fxml"));
            Parent root = loader.load();
            MenuController menuController = loader.getController();
            menuController.setStage(stage);
            Scene scene = new Scene(root);
            stage.setScene(scene);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}