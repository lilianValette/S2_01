package com.bomberman.controller;

import com.bomberman.model.Level;
import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

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
    private final IntegerProperty themeIndex = new SimpleIntegerProperty(0);
    private Level[] levels = Level.getPredefinedThemes();

    private Stage stage;
    // 0 = player, 1 = ia, 2 = theme, 3 = PLAY
    private int selectedField = 0;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    public void initialize() {
        updateUI();
        setupArrowsVisibility();

        playTextLabel.setOnMouseClicked(e -> { startGame(); });
        playTextLabel.setOnMouseEntered(e -> { selectedField = 3; updateHighlight(); });

        // Navigation clavier
        playerCountLabel.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.addEventFilter(KeyEvent.KEY_PRESSED, this::handleArrowKey);
            }
        });
    }

    private void setupArrowsVisibility() {
        // Min joueur humain = 1 si IA > 0, sinon 2
        playerLeftArrow.visibleProperty().bind(Bindings.createBooleanBinding(
                () -> playerCount.get() > (iaCount.get() == 0 ? 2 : 1),
                playerCount, iaCount
        ));
        // Max joueur humain = 2
        playerRightArrow.visibleProperty().bind(Bindings.createBooleanBinding(
                () -> playerCount.get() < 2,
                playerCount
        ));
        iaLeftArrow.visibleProperty().bind(iaCount.greaterThan(0));
        iaRightArrow.visibleProperty().bind(Bindings.createBooleanBinding(
                () -> playerCount.get() + iaCount.get() < 4,
                playerCount, iaCount
        ));
        boolean hasMultipleThemes = levels.length > 1;
        themeLeftArrow.setVisible(hasMultipleThemes);
        themeRightArrow.setVisible(hasMultipleThemes);
    }

    private void updateHighlight() {
        // Retirer sur tous
        playerTextLabel.getStyleClass().removeAll("menu-highlighted");
        iaTextLabel.getStyleClass().removeAll("menu-highlighted");
        themeTextLabel.getStyleClass().removeAll("menu-highlighted");
        playTextLabel.getStyleClass().removeAll("menu-highlighted");
        playerCountLabel.getStyleClass().removeAll("value-highlighted");
        iaCountLabel.getStyleClass().removeAll("value-highlighted");
        themeLabel.getStyleClass().removeAll("value-highlighted");

        switch (selectedField) {
            case 0:
                playerTextLabel.getStyleClass().add("menu-highlighted");
                playerCountLabel.getStyleClass().add("value-highlighted");
                break;
            case 1:
                iaTextLabel.getStyleClass().add("menu-highlighted");
                iaCountLabel.getStyleClass().add("value-highlighted");
                break;
            case 2:
                themeTextLabel.getStyleClass().add("menu-highlighted");
                themeLabel.getStyleClass().add("value-highlighted");
                break;
            case 3:
                playTextLabel.getStyleClass().add("menu-highlighted");
                break;
        }
    }

    private void updateUI() {
        playerCountLabel.setText(String.valueOf(playerCount.get()));
        iaCountLabel.setText(String.valueOf(iaCount.get()));
        themeLabel.setText(levels[themeIndex.get()].getName().toUpperCase());
        showThemePreview(levels[themeIndex.get()]);
        updateHighlight();
    }

    private void showThemePreview(Level level) {
        themePreviewPane.getChildren().clear();
        Canvas preview = GameController.createThemePreviewCanvas(level, GameController.getCellSize());
        themePreviewPane.getChildren().add(preview);
    }

    private void startGame() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/bomberman/view/game-view.fxml"));
            Parent root = loader.load();
            GameController gameController = loader.getController();
            gameController.setStage(stage);
            gameController.setTheme(levels[themeIndex.get()]);
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
            case UP:
                selectedField = (selectedField + 4 - 1) % 4;
                updateHighlight();
                break;
            case DOWN:
                selectedField = (selectedField + 1) % 4;
                updateHighlight();
                break;
            case LEFT:
                if (selectedField < 3) decrementSelected();
                break;
            case RIGHT:
                if (selectedField < 3) incrementSelected();
                break;
            case ENTER:
            case SPACE:
                if (selectedField == 3) startGame();
                break;
            default:
                return;
        }
        event.consume();
    }

    private void decrementSelected() {
        switch (selectedField) {
            case 0:
                int minPlayer = (iaCount.get() == 0) ? 2 : 1;
                if (playerCount.get() > minPlayer) {
                    playerCount.set(playerCount.get() - 1);
                    updateUI();
                }
                break;
            case 1:
                if (iaCount.get() > 0) {
                    iaCount.set(iaCount.get() - 1);
                    if (iaCount.get() == 0 && playerCount.get() < 2) {
                        playerCount.set(2);
                    }
                    updateUI();
                }
                break;
            case 2:
                themeIndex.set((themeIndex.get() - 1 + levels.length) % levels.length);
                updateUI();
                break;
        }
    }

    private void incrementSelected() {
        switch (selectedField) {
            case 0:
                if (playerCount.get() + iaCount.get() < 4) {
                    playerCount.set(playerCount.get() + 1);
                    updateUI();
                }
                break;
            case 1:
                if (playerCount.get() + iaCount.get() < 4) {
                    iaCount.set(iaCount.get() + 1);
                    updateUI();
                }
                break;
            case 2:
                themeIndex.set((themeIndex.get() + 1) % levels.length);
                updateUI();
                break;
        }
    }
}