package com.bomberman.controller;

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

/**
 * Contrôleur de la page des paramètres (Settings).
 * Modulaire : chaque paramètre (bouton, toggle...) peut être ajouté facilement.
 */
public class SettingsController {
    @FXML private StackPane rootPane;
    @FXML private ImageView backgroundImage;
    @FXML private Button levelEditorButton;
    @FXML private Button backButton;

    private Stage stage;

    public void setStage(Stage stage) {
        this.stage = stage;
        // Taille fixe cohérente
        stage.setWidth(800);
        stage.setHeight(600);
        stage.setMinWidth(800);
        stage.setMinHeight(600);
        stage.setMaxWidth(800);
        stage.setMaxHeight(600);
        stage.setResizable(false);
        stage.centerOnScreen();

        // Charger le CSS après initialisation
        Platform.runLater(this::loadStylesheet);
    }

    @FXML
    public void initialize() {
        // Fond identique au menu/account
        loadBackgroundImage();

        // Actions des boutons
        backButton.setOnAction(e -> returnToMenu());
        levelEditorButton.setOnAction(e -> openLevelEditor());

        // Prévoir ici l'ajout d'autres paramètres ou listeners
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

    private void returnToMenu() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/bomberman/view/menu.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);

            // Appliquer le CSS
            java.net.URL cssUrl = getClass().getResource("/css/style.css");
            if (cssUrl != null) {
                scene.getStylesheets().add(cssUrl.toExternalForm());
            }

            MenuController menuController = loader.getController();
            stage.setScene(scene);
            menuController.setStage(stage);

        } catch (Exception ex) {
            System.err.println("Erreur lors du retour au menu : " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void openLevelEditor() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/bomberman/view/level-editor.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);

            // Appliquer le CSS si besoin
            java.net.URL cssUrl = getClass().getResource("/css/style.css");
            if (cssUrl != null) {
                scene.getStylesheets().add(cssUrl.toExternalForm());
            }

            // Si tu crées un LevelEditorController, pense à transmettre le stage
            Object ctrl = loader.getController();
            if (ctrl instanceof LevelEditorController lec) {
                lec.setStage(stage);
            }

            stage.setScene(scene);

        } catch (Exception ex) {
            System.err.println("Erreur lors de l'ouverture de l’éditeur de niveau : " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}