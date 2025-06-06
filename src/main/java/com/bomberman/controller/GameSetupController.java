package com.bomberman.controller;

import com.bomberman.model.Theme;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class GameSetupController {
    @FXML private StackPane themePreviewPane;
    @FXML private Button playerLeft, playerRight, iaLeft, iaRight, themeLeft, themeRight, validateButton;
    @FXML private Label playerCountLabel, iaCountLabel, themeLabel;

    private int playerCount = 2;
    private int iaCount = 0;
    private int themeIndex = 0;
    private Theme[] themes = Theme.getPredefinedThemes();
    private Stage stage;

    // Pour la preview
    private final int previewCellSize = 32;

    public void setStage(Stage stage) { this.stage = stage; }

    @FXML
    public void initialize() {
        updateUI();

        playerLeft.setOnAction(e -> { playerCount = Math.max(1, playerCount-1); updateUI(); });
        playerRight.setOnAction(e -> { playerCount = Math.min(4, playerCount+1); updateUI(); });
        iaLeft.setOnAction(e -> { iaCount = Math.max(0, iaCount-1); updateUI(); });
        iaRight.setOnAction(e -> { iaCount = Math.min(4, iaCount+1); updateUI(); });
        themeLeft.setOnAction(e -> { themeIndex = (themeIndex - 1 + themes.length) % themes.length; updateUI(); });
        themeRight.setOnAction(e -> { themeIndex = (themeIndex + 1) % themes.length; updateUI(); });

        validateButton.setOnAction(e -> startGame());
    }

    private void updateUI() {
        playerCountLabel.setText(String.valueOf(playerCount));
        iaCountLabel.setText(String.valueOf(iaCount));
        themeLabel.setText(themes[themeIndex].getName().toUpperCase());
        showThemePreview(themes[themeIndex]);
    }

    private void showThemePreview(Theme theme) {
        themePreviewPane.getChildren().clear();
        Canvas preview = GameController.createThemePreviewCanvas(theme, previewCellSize);
        themePreviewPane.getChildren().add(preview);
    }

    private void startGame() {
        // TODO : Passer les paramètres à GameController
        // Exemple : loader.setPlayerCount(playerCount); loader.setAI(iaCount); loader.setTheme(themes[themeIndex]);
        // Puis fermer la fenêtre de sélection
    }
}