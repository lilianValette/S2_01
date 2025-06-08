package com.bomberman.controller;

import com.bomberman.model.LevelEditor;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class LevelEditorController {

    @FXML private StackPane rootPane;
    @FXML private ImageView backgroundImage;
    @FXML private Button saveButton;
    @FXML private Button clearButton;
    @FXML private Button backButton;

    // Exemple d'intégration de la grille (à compléter avec une GridPane, etc.)
    // @FXML private GridPane gridPane;

    private Stage stage;
    private LevelEditor levelEditor;

    // Dimensions par défaut, à adapter si besoin
    private static final int DEFAULT_WIDTH = 13;
    private static final int DEFAULT_HEIGHT = 11;

    public void setStage(Stage stage) {
        this.stage = stage;
        stage.setWidth(800);
        stage.setHeight(600);
        stage.setMinWidth(800);
        stage.setMinHeight(600);
        stage.setMaxWidth(800);
        stage.setMaxHeight(600);
        stage.setResizable(false);
        stage.centerOnScreen();

        Platform.runLater(this::loadStylesheet);
    }

    @FXML
    public void initialize() {
        // Chargement image de fond
        loadBackgroundImage();

        // Initialise le modèle d'édition de niveau
        levelEditor = new LevelEditor(DEFAULT_WIDTH, DEFAULT_HEIGHT);

        // Brancher les boutons
        saveButton.setOnAction(e -> saveLevel());
        clearButton.setOnAction(e -> clearLevel());
        backButton.setOnAction(e -> returnToSettings());

        // À compléter : initialisation de la grille dans l'UI
        // initializeGridUI();
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

    private void loadStylesheet() {
        try {
            java.net.URL cssUrl = getClass().getResource("/css/style.css");
            if (cssUrl != null && stage.getScene() != null) {
                stage.getScene().getStylesheets().clear();
                stage.getScene().getStylesheets().add(cssUrl.toExternalForm());
            }
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement du CSS : " + e.getMessage());
        }
    }

    private void saveLevel() {
        // Exemple : sauvegarder sous un nom fixe ou avec un FileChooser (à compléter)
        String filename = "custom_level.lvl";
        levelEditor.saveLevel(filename);
        // Feedback utilisateur à ajouter (popup, label…)
        System.out.println("Niveau sauvegardé sous : " + filename);
    }

    private void clearLevel() {
        levelEditor.clear();
        // À compléter : réinitialiser la grille graphique
        System.out.println("Niveau effacé.");
    }

    private void returnToSettings() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/bomberman/view/settings.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);

            java.net.URL cssUrl = getClass().getResource("/css/style.css");
            if (cssUrl != null) {
                scene.getStylesheets().add(cssUrl.toExternalForm());
            }

            SettingsController settingsController = loader.getController();
            settingsController.setStage(stage);

            stage.setScene(scene);
        } catch (Exception ex) {
            System.err.println("Erreur lors du retour aux paramètres : " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    // À compléter : méthode pour interagir avec la grille
    // private void initializeGridUI() { ... }
    // private void handleCellClick(int x, int y) { ... }
}