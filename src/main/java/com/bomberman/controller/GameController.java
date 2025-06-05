package com.bomberman.controller;

import com.bomberman.model.Game;
import com.bomberman.model.Player;
import com.bomberman.model.Bomb;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;

public class GameController {
    @FXML
    private Canvas gameCanvas;

    private Game game;
    private final int CELL_SIZE = 32;
    private final int BORDER_SIZE = 1;
    private final double BORDER_PIXEL_RATIO = 0.5;
    private final double TOP_UI_HEIGHT_RATIO = 2.5;
    private Timeline timeline;
    private int timerSeconds = 120; // Timer en secondes (2:00)

    @FXML
    public void initialize() {
        game = new Game(15, 13, 2);

        // Calcul dynamique des dimensions
        int gridWidth = game.getGrid().getWidth();
        int gridHeight = game.getGrid().getHeight();
        double borderPixel = CELL_SIZE * BORDER_PIXEL_RATIO;
        double topUiHeight = CELL_SIZE * TOP_UI_HEIGHT_RATIO;
        double canvasWidth = borderPixel + gridWidth * CELL_SIZE + borderPixel;
        double canvasHeight = topUiHeight + borderPixel + gridHeight * CELL_SIZE + borderPixel;

        gameCanvas.setWidth(canvasWidth);
        gameCanvas.setHeight(canvasHeight);

        drawGrid();

        gameCanvas.setFocusTraversable(true);
        gameCanvas.setOnKeyPressed(this::handleKeyPressed);

        timeline = new Timeline(new KeyFrame(Duration.seconds(0.5), e -> {
            game.updateBombs();
            drawGrid();
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();

        // Timer pour décompte du temps (1 seconde)
        Timeline timerTimeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            if (timerSeconds > 0) {
                timerSeconds--;
                drawGrid();
            }
        }));
        timerTimeline.setCycleCount(Timeline.INDEFINITE);
        timerTimeline.play();
    }

    private void handleKeyPressed(KeyEvent event) {
        Player p1 = game.getPlayers().get(0);
        Player p2 = game.getPlayers().size() > 1 ? game.getPlayers().get(1) : null;

        switch (event.getCode()) {
            // Joueur 1 (flèches + espace)
            case UP    -> { if (p1.isAlive()) game.movePlayer(p1, 0, -1); }
            case DOWN  -> { if (p1.isAlive()) game.movePlayer(p1, 0,  1); }
            case LEFT  -> { if (p1.isAlive()) game.movePlayer(p1, -1, 0); }
            case RIGHT -> { if (p1.isAlive()) game.movePlayer(p1, 1,  0); }
            case SPACE -> { if (p1.isAlive()) game.placeBomb(p1); }

            // Joueur 2 (ZQSD + SHIFT)
            case Z -> { if (p2 != null && p2.isAlive()) game.movePlayer(p2, 0, -1); }
            case S -> { if (p2 != null && p2.isAlive()) game.movePlayer(p2, 0,  1); }
            case Q -> { if (p2 != null && p2.isAlive()) game.movePlayer(p2, -1, 0); }
            case D -> { if (p2 != null && p2.isAlive()) game.movePlayer(p2, 1,  0); }
            case SHIFT -> { if (p2 != null && p2.isAlive()) game.placeBomb(p2); }
        }
        drawGrid();
    }

    private void drawGrid() {
        GraphicsContext gc = gameCanvas.getGraphicsContext2D();
        var grid = game.getGrid();

        int gridWidth = grid.getWidth();
        int gridHeight = grid.getHeight();
        double borderPixel = CELL_SIZE * BORDER_PIXEL_RATIO;
        double topUiHeight = CELL_SIZE * TOP_UI_HEIGHT_RATIO;
        double canvasWidth = borderPixel + gridWidth * CELL_SIZE + borderPixel;

        // Efface tout
        gc.clearRect(0, 0, gameCanvas.getWidth(), gameCanvas.getHeight());

        // Bande orange en haut
        gc.setFill(Color.ORANGE);
        gc.fillRect(0, 0, gameCanvas.getWidth(), topUiHeight);

        // --- Affichage du timer centré dans la bande orange, avec fond blanc et cadre foncé ---
        String timerStr = String.format("%d:%02d", timerSeconds / 60, timerSeconds % 60);
        gc.setFont(Font.font("Consolas", topUiHeight * 0.4)); // Taille adaptative
        Text text = new Text(timerStr);
        text.setFont(gc.getFont());
        double timerWidth = text.getLayoutBounds().getWidth();
        double timerHeight = text.getLayoutBounds().getHeight();
        double timerX = (canvasWidth - timerWidth) / 2;
        double timerY = (topUiHeight / 2) + (timerHeight / 4);

        // Fond blanc + cadre foncé pour le timer
        double paddingX = 18;
        double paddingY = 10;
        double bgWidth = timerWidth + 2 * paddingX;
        double bgHeight = timerHeight + 2 * paddingY;
        double bgX = timerX - paddingX;
        double bgY = timerY - timerHeight - paddingY / 2;

        // Cadre foncé
        gc.setStroke(Color.rgb(34, 34, 34));
        gc.setLineWidth(2);
        gc.strokeRoundRect(bgX, bgY, bgWidth, bgHeight, 12, 12);

        // Fond blanc
        gc.setFill(Color.WHITE);
        gc.fillRoundRect(bgX, bgY, bgWidth, bgHeight, 12, 12);

        // Texte (timer)
        gc.setFill(Color.BLACK);
        gc.fillText(timerStr, timerX, timerY);

        // --- Affichage des vies des joueurs dans la bande orange ---
        double iconSize = topUiHeight * 0.5;
        double iconY = (topUiHeight - iconSize) / 2 + iconSize * 0.8; // Centrage vertical, ajusté pour la baseline

        // Joueur 1 (bleu, à gauche)
        Player p1 = game.getPlayers().get(0);
        gc.setFont(Font.font("Arial", iconSize * 0.8));
        gc.setFill(Color.BLUE);
        String p1LivesStr = "♥ " + p1.getLives();
        double p1TextWidth = new Text(p1LivesStr).getLayoutBounds().getWidth();
        double p1X = bgX - 80 - p1TextWidth;
        gc.fillText(p1LivesStr, Math.max(15, p1X), iconY);

        // Joueur 2 (rouge, à droite) si présent
        if (game.getPlayers().size() > 1) {
            Player p2 = game.getPlayers().get(1);
            gc.setFill(Color.RED);
            String p2LivesStr = p2.getLives() + " ♥";
            double p2TextWidth = new Text(p2LivesStr).getLayoutBounds().getWidth();
            double p2X = bgX + bgWidth + 50;
            gc.fillText(p2LivesStr, Math.min(canvasWidth - p2TextWidth - 15, p2X), iconY);
        }

        // --- Bordures gris très foncé autour de la grille (sous la zone orange) ---
        gc.setFill(Color.rgb(34, 34, 34));
        // Haut (juste sous la zone orange)
        gc.fillRect(0, topUiHeight, canvasWidth, borderPixel);
        // Bas
        gc.fillRect(0, topUiHeight + borderPixel + gridHeight * CELL_SIZE, canvasWidth, borderPixel);
        // Gauche
        gc.fillRect(0, topUiHeight, borderPixel, borderPixel + gridHeight * CELL_SIZE);
        // Droite
        gc.fillRect(borderPixel + gridWidth * CELL_SIZE, topUiHeight, borderPixel, borderPixel + gridHeight * CELL_SIZE);

        // --- Dessine la grille ---
        for (int y = 0; y < gridHeight; y++) {
            for (int x = 0; x < gridWidth; x++) {
                double drawX = borderPixel + x * CELL_SIZE;
                double drawY = topUiHeight + borderPixel + y * CELL_SIZE;

                switch (grid.getCell(x, y)) {
                    case INDESTRUCTIBLE -> gc.setFill(Color.DARKGRAY); // Couleur d'origine pour les murs indestructibles
                    case DESTRUCTIBLE   -> gc.setFill(Color.BURLYWOOD);
                    case BOMB           -> gc.setFill(Color.BLACK);
                    case EXPLOSION      -> gc.setFill(Color.ORANGE);
                    default             -> gc.setFill(Color.rgb(0, 128, 64)); // Vert pelouse
                }
                gc.fillRect(drawX, drawY, CELL_SIZE, CELL_SIZE);
                gc.setStroke(Color.BLACK);
                gc.strokeRect(drawX, drawY, CELL_SIZE, CELL_SIZE);
            }
        }
        // --- Dessine les bombes (optionnel) ---
        for (Bomb b : game.getBombs()) {
            double bx = borderPixel + b.getX() * CELL_SIZE + 8;
            double by = topUiHeight + borderPixel + b.getY() * CELL_SIZE + 8;
            gc.setFill(Color.BLACK);
            gc.fillOval(bx, by, CELL_SIZE - 16, CELL_SIZE - 16);
        }
        // --- Dessine les joueurs ---
        for (Player p : game.getPlayers()) {
            if (p.isAlive()) {
                double px = borderPixel + p.getX() * CELL_SIZE + 4;
                double py = topUiHeight + borderPixel + p.getY() * CELL_SIZE + 4;
                gc.setFill(p.getId() == 1 ? Color.BLUE : Color.RED);
                gc.fillOval(px, py, CELL_SIZE - 8, CELL_SIZE - 8);
                // L'affichage des vies dans la grille est supprimé !
            }
        }
    }
}