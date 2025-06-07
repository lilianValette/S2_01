package com.bomberman.controller;

import com.bomberman.model.Level;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Label;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/**
 * Contrôleur de l'écran de configuration de la partie.
 * Affiche un aperçu du niveau en fond et adapte parfaitement la taille de la fenêtre à l'aperçu.
 */
public class GameSetupController {

    @FXML private StackPane themePreviewPane;
    @FXML private Label
            playerTextLabel, iaTextLabel, themeTextLabel, playTextLabel,
            playerLeftArrow, playerRightArrow,
            iaLeftArrow, iaRightArrow,
            themeLeftArrow, themeRightArrow,
            playerCountLabel, iaCountLabel, themeLabel;

    private final IntegerProperty playerCount = new SimpleIntegerProperty(2);
    private final IntegerProperty iaCount = new SimpleIntegerProperty(0);
    private final IntegerProperty levelIndex = new SimpleIntegerProperty(0);
    private final Level[] levels = Level.getPredefinedLevels();

    private Stage stage;
    private int selectedField = 0; // 0 = player, 1 = ia, 2 = theme, 3 = PLAY

    public void setStage(Stage stage) {
        this.stage = stage;
        // On tente d'adapter la taille dès que possible
        Platform.runLater(this::adaptStageToPreview);
    }

    @FXML
    public void initialize() {
        updateUI();
        setupArrowsVisibility();
        playTextLabel.setOnMouseClicked(e -> startGame());
        playTextLabel.setOnMouseEntered(e -> { selectedField = 3; updateHighlight(); });

        playerCountLabel.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.addEventFilter(KeyEvent.KEY_PRESSED, this::handleArrowKey);
            }
        });
    }

    private void setupArrowsVisibility() {
        playerLeftArrow.visibleProperty().bind(Bindings.createBooleanBinding(
                () -> playerCount.get() > (iaCount.get() == 0 ? 2 : 1),
                playerCount, iaCount
        ));
        playerRightArrow.visibleProperty().bind(playerCount.lessThan(2));
        iaLeftArrow.visibleProperty().bind(iaCount.greaterThan(0));
        iaRightArrow.visibleProperty().bind(Bindings.createBooleanBinding(
                () -> playerCount.get() + iaCount.get() < 4,
                playerCount, iaCount
        ));
        boolean hasMultipleLevels = levels.length > 1;
        themeLeftArrow.setVisible(hasMultipleLevels);
        themeRightArrow.setVisible(hasMultipleLevels);
    }

    private void updateHighlight() {
        playerTextLabel.getStyleClass().removeAll("menu-highlighted");
        iaTextLabel.getStyleClass().removeAll("menu-highlighted");
        themeTextLabel.getStyleClass().removeAll("menu-highlighted");
        playTextLabel.getStyleClass().removeAll("menu-highlighted");
        playerCountLabel.getStyleClass().removeAll("value-highlighted");
        iaCountLabel.getStyleClass().removeAll("value-highlighted");
        themeLabel.getStyleClass().removeAll("value-highlighted");

        switch (selectedField) {
            case 0 -> {
                playerTextLabel.getStyleClass().add("menu-highlighted");
                playerCountLabel.getStyleClass().add("value-highlighted");
            }
            case 1 -> {
                iaTextLabel.getStyleClass().add("menu-highlighted");
                iaCountLabel.getStyleClass().add("value-highlighted");
            }
            case 2 -> {
                themeTextLabel.getStyleClass().add("menu-highlighted");
                themeLabel.getStyleClass().add("value-highlighted");
            }
            case 3 -> playTextLabel.getStyleClass().add("menu-highlighted");
        }
    }

    private void updateUI() {
        playerCountLabel.setText(String.valueOf(playerCount.get()));
        iaCountLabel.setText(String.valueOf(iaCount.get()));
        themeLabel.setText(levels[levelIndex.get()].getName().toUpperCase());
        showLevelPreview(levels[levelIndex.get()]);
        updateHighlight();
    }

    private void showLevelPreview(Level level) {
        themePreviewPane.getChildren().clear();
        Canvas preview = GameController.createLevelPreviewCanvas(level, GameController.getCellSize());

        // Bind le canvas pour qu'il occupe tout l'espace du StackPane
        preview.widthProperty().addListener((obs, oldW, newW) -> adaptStageToPreview());
        preview.heightProperty().addListener((obs, oldH, newH) -> adaptStageToPreview());

        // S'assurer que le canvas n'a pas de marge et est bien positionné
        StackPane.setAlignment(preview, javafx.geometry.Pos.CENTER);
        StackPane.setMargin(preview, javafx.geometry.Insets.EMPTY);

        themePreviewPane.getChildren().add(0, preview);

        // On attend la fin du layout pour ajuster la taille de la fenêtre exactement (évite les coupes)
        Platform.runLater(this::adaptStageToPreview);
    }

    /**
     * Adapte la taille de la fenêtre exactement à celle du canvas de fond.
     * Utilise runLater pour être sûr d'agir après le layout.
     */
    private void adaptStageToPreview() {
        if (stage != null && !themePreviewPane.getChildren().isEmpty()) {
            Canvas preview = (Canvas) themePreviewPane.getChildren().get(0);
            double w = preview.getWidth();
            double h = preview.getHeight();

            // Ajoute une petite marge de sécurité si besoin (dépend du rendu, à ajuster si nécessaire)
            double fudge = 0.5; // Corrige les problèmes de découpe liés au rendu pixel-perfect

            if (w > 0 && h > 0) {
                stage.setMinWidth(w + fudge);
                stage.setMinHeight(h + fudge);
                stage.setMaxWidth(w + fudge);
                stage.setMaxHeight(h + fudge);
                stage.setWidth(w + fudge);
                stage.setHeight(h + fudge);
                stage.setResizable(false);
            }
        }
    }

    private void startGame() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/bomberman/view/game-view.fxml"));
            Parent root = loader.load();
            GameController gameController = loader.getController();
            gameController.setStage(stage);
            gameController.setLevel(levels[levelIndex.get()]);
            gameController.setPlayerCount(playerCount.get());
            gameController.setIaCount(iaCount.get());
            gameController.startGame();
            stage.setScene(new Scene(root));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void handleArrowKey(KeyEvent event) {
        switch (event.getCode()) {
            case UP    -> { selectedField = (selectedField + 3) % 4; updateHighlight(); }
            case DOWN  -> { selectedField = (selectedField + 1) % 4; updateHighlight(); }
            case LEFT  -> { if (selectedField < 3) decrementSelected(); }
            case RIGHT -> { if (selectedField < 3) incrementSelected(); }
            case ENTER, SPACE -> { if (selectedField == 3) startGame(); }
            default -> { return; }
        }
        event.consume();
    }

    private void decrementSelected() {
        switch (selectedField) {
            case 0 -> {
                int minPlayer = (iaCount.get() == 0) ? 2 : 1;
                if (playerCount.get() > minPlayer) {
                    playerCount.set(playerCount.get() - 1);
                    updateUI();
                }
            }
            case 1 -> {
                if (iaCount.get() > 0) {
                    iaCount.set(iaCount.get() - 1);
                    if (iaCount.get() == 0 && playerCount.get() < 2) {
                        playerCount.set(2);
                    }
                    updateUI();
                }
            }
            case 2 -> {
                levelIndex.set((levelIndex.get() - 1 + levels.length) % levels.length);
                updateUI();
            }
        }
    }

    private void incrementSelected() {
        switch (selectedField) {
            case 0 -> {
                if (playerCount.get() < 2 && playerCount.get() + iaCount.get() < 4) {
                    playerCount.set(playerCount.get() + 1);
                    updateUI();
                }
            }
            case 1 -> {
                if (playerCount.get() + iaCount.get() < 4) {
                    iaCount.set(iaCount.get() + 1);
                    updateUI();
                }
            }
            case 2 -> {
                levelIndex.set((levelIndex.get() + 1) % levels.length);
                updateUI();
            }
        }
    }
}