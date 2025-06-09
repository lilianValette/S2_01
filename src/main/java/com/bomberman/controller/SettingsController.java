package com.bomberman.controller;

import com.bomberman.model.AIDifficulty;
import com.bomberman.model.GameSettings;
import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class SettingsController {
    @FXML private StackPane rootPane;
    @FXML private ImageView backgroundImage;
    @FXML private Button levelEditorButton;
    @FXML private Button backButton;

    // IA level
    @FXML private HBox aiLevelBox;
    @FXML private Label aiLevelTextLabel;
    @FXML private Label aiLevelLeftArrow;
    @FXML private Label aiLevelLabel;
    @FXML private Label aiLevelRightArrow;

    private final IntegerProperty aiLevelIndex = GameSettings.aiLevelIndexProperty(); // BIND GLOBAL
    private int selectedField = 0; // 1 si focus IA

    private Stage stage;

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
        loadBackgroundImage();

        backButton.setOnAction(e -> returnToMenu());
        levelEditorButton.setOnAction(e -> openLevelEditor());

        // --- Liaison BINDING du label sur la propriété globale ---
        aiLevelLabel.textProperty().bind(aiLevelIndex.asString().map(idx -> GameSettings.AI_LEVELS[Integer.parseInt(idx)]));

        aiLevelLeftArrow.setOnMouseClicked(e -> { selectedField = 1; updateAILevelHighlight(); decrementAiLevel(); });
        aiLevelRightArrow.setOnMouseClicked(e -> { selectedField = 1; updateAILevelHighlight(); incrementAiLevel(); });
        aiLevelBox.setOnMouseEntered(e -> {
            selectedField = 1;
            updateAILevelHighlight();
        });
        levelEditorButton.setOnMouseEntered(e -> {
            selectedField = 0;
            updateAILevelHighlight();
            levelEditorButton.requestFocus();
        });

        aiLevelLabel.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.addEventFilter(KeyEvent.KEY_PRESSED, this::handleArrowKey);
            }
        });

        aiLevelLeftArrow.visibleProperty().bind(aiLevelIndex.greaterThan(0));
        aiLevelRightArrow.visibleProperty().bind(aiLevelIndex.lessThan(GameSettings.AI_LEVELS.length - 1));

        updateAILevelHighlight();
    }

    private void updateAILevelHighlight() {
        aiLevelTextLabel.getStyleClass().removeAll("menu-highlighted");
        aiLevelLabel.getStyleClass().removeAll("value-highlighted");
        levelEditorButton.getStyleClass().remove("menu-highlighted");
        if (selectedField == 1) {
            aiLevelTextLabel.getStyleClass().add("menu-highlighted");
            aiLevelLabel.getStyleClass().add("value-highlighted");
        } else {
            levelEditorButton.getStyleClass().add("menu-highlighted");
        }
    }

    private void handleArrowKey(KeyEvent event) {
        switch (event.getCode()) {
            case UP, DOWN -> {
                selectedField = (selectedField == 0) ? 1 : 0;
                updateAILevelHighlight();
                if (selectedField == 0) {
                    levelEditorButton.requestFocus();
                } else {
                    aiLevelBox.requestFocus();
                }
            }
            case LEFT -> { if (selectedField == 1) decrementAiLevel(); }
            case RIGHT -> { if (selectedField == 1) incrementAiLevel(); }
            case ENTER, SPACE -> {
                if (selectedField == 0) {
                    openLevelEditor();
                }
            }
            default -> { return; }
        }
        event.consume();
    }

    private void decrementAiLevel() {
        if (aiLevelIndex.get() > 0) {
            aiLevelIndex.set(aiLevelIndex.get() - 1);
        }
    }

    private void incrementAiLevel() {
        if (aiLevelIndex.get() < GameSettings.AI_LEVELS.length - 1) {
            aiLevelIndex.set(aiLevelIndex.get() + 1);
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

    private void loadStylesheet() {
        try {
            java.net.URL cssUrl = getClass().getResource("/css/settings-menu.css");
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
            java.net.URL cssUrl = getClass().getResource("/css/settings-menu.css");
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
            java.net.URL cssUrl = getClass().getResource("/css/settings-menu.css");
            if (cssUrl != null) {
                scene.getStylesheets().add(cssUrl.toExternalForm());
            }
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