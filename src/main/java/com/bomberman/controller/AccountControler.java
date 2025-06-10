
package com.bomberman.controller;

import com.bomberman.model.User;
import com.bomberman.model.UserManager;
import javafx.application.Platform;
import javafx.concurrent.Task;
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

    // Éléments de profil - SUPPRESSION DU SCORE TOTAL
    @FXML private Label profileUsername;
    @FXML private Label profileGamesPlayed;
    @FXML private Label profileGamesWon;
    @FXML private Label profileWinRate;
    // @FXML private Label profileTotalScore; // SUPPRIMÉ
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

        // Charger le fichier CSS après que la scène soit définie
        Platform.runLater(() -> {
            loadStylesheet();
            // Re-appliquer les styles des boutons après le chargement du CSS
            setupButtonStyles();
        });
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

        // Appliquer les styles des boutons après l'initialisation
        Platform.runLater(() -> setupButtonStyles());
    }

    private void loadStylesheet() {
        try {
            // Charger le fichier CSS principal du jeu
            java.net.URL cssUrl = getClass().getResource("/css/style.css");
            if (cssUrl != null) {
                String cssPath = cssUrl.toExternalForm();
                if (stage.getScene() != null) {
                    stage.getScene().getStylesheets().clear(); // Nettoyer les anciens styles
                    stage.getScene().getStylesheets().add(cssPath);
                    System.out.println("CSS chargé avec succès : " + cssPath);
                } else {
                    System.err.println("Scene est null, impossible de charger le CSS");
                }
            } else {
                System.err.println("Fichier CSS non trouvé : /css/style.css");
            }
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement du CSS : " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void setupButtonStyles() {
        // Attendre que la scène soit prête
        Platform.runLater(() -> {
            // Supprimer les classes par défaut et appliquer les bonnes classes CSS
            setupButtonStyle(loginButton, "account-button");
            setupButtonStyle(createButton, "account-button");
            setupButtonStyle(showCreateButton, "account-button");
            setupButtonStyle(showLoginButton, "account-button");
            setupButtonStyle(logoutButton, "game-button-danger");
            setupButtonStyle(backButton, "game-button-secondary");

            // Forcer la mise à jour des styles
            if (stage.getScene() != null && stage.getScene().getRoot() != null) {
                stage.getScene().getRoot().applyCss();
            }
        });
    }

    private void setupButtonStyle(Button button, String styleClass) {
        if (button != null) {
            // Debug : afficher les classes actuelles
            System.out.println("Avant - Bouton " + button.getId() + " classes : " + button.getStyleClass());

            // Nettoyer toutes les classes existantes sauf "button" si nécessaire
            button.getStyleClass().removeAll("button", "account-button", "game-button-danger", "game-button-secondary");

            // Ajouter la classe spécifique
            button.getStyleClass().add(styleClass);

            // Debug : afficher les classes après modification
            System.out.println("Après - Bouton " + button.getId() + " classes : " + button.getStyleClass());

            // Forcer la mise à jour du style
            button.applyCss();
        }
    }

    private void loadBackgroundImage() {
        try {
            java.net.URL url = getClass().getResource("/images/menu/Bomber_fond.jpg");
            if (url != null) {
                Image image = new Image(url.toExternalForm());
                backgroundImage.setImage(image);
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
        setContainerVisibility(true, false, false);
        clearMessages();
        clearLoginForm();
    }

    private void showCreateView() {
        setContainerVisibility(false, true, false);
        clearMessages();
        clearCreateForm();
    }

    private void showProfileView() {
        setContainerVisibility(false, false, true);
        updateProfileInfo();
    }

    private void setContainerVisibility(boolean login, boolean create, boolean profile) {
        loginContainer.setVisible(login);
        createContainer.setVisible(create);
        profileContainer.setVisible(profile);
    }

    private void handleLogin() {
        String username = loginUsername.getText().trim();
        String password = loginPassword.getText();

        if (username.isEmpty() || password.isEmpty()) {
            showLoginMessage("Veuillez remplir tous les champs.", true);
            return;
        }

        // Désactiver le bouton pendant la connexion
        loginButton.setDisable(true);

        if (userManager.login(username, password)) {
            showLoginMessage("Connexion réussie ! Bienvenue " + username, false);

            // Utiliser un Task pour gérer le délai sans bloquer l'UI
            Task<Void> delayTask = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    Thread.sleep(1000);
                    return null;
                }

                @Override
                protected void succeeded() {
                    Platform.runLater(() -> {
                        updateUI();
                        loginButton.setDisable(false);
                    });
                }
            };

            new Thread(delayTask).start();
        } else {
            showLoginMessage("Nom d'utilisateur ou mot de passe incorrect.", true);
            loginButton.setDisable(false);
        }
    }

    private void handleCreateAccount() {
        String username = createUsername.getText().trim();
        String password = createPassword.getText();
        String confirm = confirmPassword.getText();

        if (!validateAccountCreation(username, password, confirm)) {
            return;
        }

        createButton.setDisable(true);

        if (userManager.createAccount(username, password)) {
            showCreateMessage("Compte créé avec succès ! Vous pouvez maintenant vous connecter.", false);

            // Utiliser un Task pour gérer le délai
            Task<Void> delayTask = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    Thread.sleep(1500);
                    return null;
                }

                @Override
                protected void succeeded() {
                    Platform.runLater(() -> {
                        showLoginView();
                        createButton.setDisable(false);
                    });
                }
            };

            new Thread(delayTask).start();
        } else {
            showCreateMessage("Ce nom d'utilisateur est déjà pris.", true);
            createButton.setDisable(false);
        }
    }

    private boolean validateAccountCreation(String username, String password, String confirm) {
        if (username.isEmpty() || password.isEmpty() || confirm.isEmpty()) {
            showCreateMessage("Veuillez remplir tous les champs.", true);
            return false;
        }

        if (username.length() < 3) {
            showCreateMessage("Le nom d'utilisateur doit contenir au moins 3 caractères.", true);
            return false;
        }

        if (password.length() < 4) {
            showCreateMessage("Le mot de passe doit contenir au moins 4 caractères.", true);
            return false;
        }

        if (!password.equals(confirm)) {
            showCreateMessage("Les mots de passe ne correspondent pas.", true);
            return false;
        }

        return true;
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

        boolean isValid = username.length() >= 3 &&
                password.length() >= 4 &&
                password.equals(confirm) &&
                !confirm.isEmpty();

        createButton.setDisable(!isValid);
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
            // SUPPRESSION DE L'AFFICHAGE DU SCORE TOTAL
            // profileTotalScore.setText(String.valueOf(user.getTotalScore()));
        }
    }

    private void showLoginMessage(String message, boolean isError) {
        setMessageStyle(loginMessage, message, isError);
    }

    private void showCreateMessage(String message, boolean isError) {
        setMessageStyle(createMessage, message, isError);
    }

    private void setMessageStyle(Label messageLabel, String message, boolean isError) {
        messageLabel.setText(message);
        messageLabel.getStyleClass().clear();
        if (isError) {
            messageLabel.getStyleClass().add("error-message");
        } else {
            messageLabel.getStyleClass().add("success-message");
        }
    }

    private void clearMessages() {
        clearMessage(loginMessage);
        clearMessage(createMessage);
    }

    private void clearMessage(Label messageLabel) {
        messageLabel.setText("");
        messageLabel.getStyleClass().removeAll("success-message", "error-message");
    }

    private void clearLoginForm() {
        loginUsername.clear();
        loginPassword.clear();
    }

    private void clearCreateForm() {
        createUsername.clear();
        createPassword.clear();
        confirmPassword.clear();
        clearMessage(createMessage);
        // Réactiver le bouton si il était désactivé
        createButton.setDisable(true); // Il sera réactivé par la validation
    }

    private void clearAllForms() {
        clearLoginForm();
        clearCreateForm();
        clearMessages();
    }

    private void returnToMenu() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/bomberman/view/menu.fxml"));
            Parent root = loader.load();

            // Créer une nouvelle scène avec le CSS
            Scene scene = new Scene(root);

            // Appliquer le CSS à la nouvelle scène
            try {
                java.net.URL cssUrl = getClass().getResource("/css/style.css");
                if (cssUrl != null) {
                    String cssPath = cssUrl.toExternalForm();
                    scene.getStylesheets().add(cssPath);
                    System.out.println("CSS appliqué au menu : " + cssPath);
                } else {
                    System.err.println("CSS non trouvé pour le menu");
                }
            } catch (Exception e) {
                System.err.println("Erreur lors du chargement du CSS pour le menu : " + e.getMessage());
            }

            // Configurer le contrôleur du menu
            MenuController menuController = loader.getController();
            stage.setScene(scene);
            menuController.setStage(stage);

        } catch (Exception ex) {
            System.err.println("Erreur lors du retour au menu : " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}