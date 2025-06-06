package com.bomberman.controller;

import com.bomberman.model.Theme;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class GameSetupController {
    @FXML private StackPane themePreviewPane;
    @FXML private Button playerLeft, playerRight, iaLeft, iaRight, themeLeft, themeRight, playButton;
    @FXML private Label playerCountLabel, iaCountLabel, themeLabel;

    private int playerCount = 2;
    private int iaCount = 0;
    private int themeIndex = 0;
    private Theme[] themes = Theme.getPredefinedThemes();

    private Stage stage;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    public void initialize() {
        updateUI();

        playerLeft.setOnAction(e -> {
            if (playerCount > 1) {
                playerCount--;
                updateUI();
            }
        });
        playerRight.setOnAction(e -> {
            if (playerCount + iaCount < 4) {
                playerCount++;
                updateUI();
            }
        });
        iaLeft.setOnAction(e -> {
            if (iaCount > 0) {
                iaCount--;
                updateUI();
            }
        });
        iaRight.setOnAction(e -> {
            if (playerCount + iaCount < 4) {
                iaCount++;
                updateUI();
            }
        });
        themeLeft.setOnAction(e -> {
            themeIndex = (themeIndex - 1 + themes.length) % themes.length;
            updateUI();
        });
        themeRight.setOnAction(e -> {
            themeIndex = (themeIndex + 1) % themes.length;
            updateUI();
        });
        playButton.setOnAction(e -> startGame());
    }

    private void updateUI() {
        playerCountLabel.setText(String.valueOf(playerCount));
        iaCountLabel.setText(String.valueOf(iaCount));
        themeLabel.setText(themes[themeIndex].getName().toUpperCase());
        showThemePreview(themes[themeIndex]);
    }

    private void showThemePreview(Theme theme) {
        themePreviewPane.getChildren().clear();
        Canvas preview = GameController.createThemePreviewCanvas(theme, GameController.getCellSize());
        themePreviewPane.getChildren().add(preview);
    }

    private void startGame() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/bomberman/view/game-view.fxml"));
            Parent root = loader.load();
            GameController gameController = loader.getController();
            gameController.setStage(stage);
            gameController.setTheme(themes[themeIndex]);
            gameController.setPlayerCount(playerCount);
            gameController.setIaCount(iaCount);
            gameController.startGame(); // <== Initialisation réelle ici
            stage.setScene(new Scene(root));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}