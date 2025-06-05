package com.bomberman.controller;

import com.bomberman.model.Game;
import com.bomberman.model.Player;
import com.bomberman.model.Bomb;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.scene.image.Image;

public class GameController {
    @FXML
    private Canvas gameCanvas;

    private Stage stage;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    private Game game;
    private final int CELL_SIZE = 32;
    private final int BORDER_SIZE = 1;
    private final double BORDER_PIXEL_RATIO = 0.5;
    private final double TOP_UI_HEIGHT_RATIO = 2.5;
    private Timeline timeline;
    private Timeline timerTimeline;
    private int timerSeconds = 120; // Timer en secondes (2:00)
    private Image avatarP1;
    private Image avatarP2;

    // Images pour les murs
    private Image wallIndestructibleImg;
    private Image wallDestructibleImg;

    @FXML
    public void initialize() {
        game = new Game(15, 13, 2);

        // Charge les images d'avatar et de murs (mets tes propres chemins si besoin)
        avatarP1 = new Image(getClass().getResourceAsStream("/images/avatarsJoueurs/avatarBleu.png"));
        avatarP2 = new Image(getClass().getResourceAsStream("/images/avatarsJoueurs/avatarRouge.png"));
        wallIndestructibleImg = new Image(getClass().getResourceAsStream("/images/elementsMap/murIndestructible.png"));
        wallDestructibleImg = new Image(getClass().getResourceAsStream("/images/elementsMap/murDestructible.png"));

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
        timerTimeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            if (timerSeconds > 0) {
                timerSeconds--;
                drawGrid();
            }
        }));
        timerTimeline.setCycleCount(Timeline.INDEFINITE);
        timerTimeline.play();
    }

    private void returnToMenu() {
        // Stoppe toutes les timelines pour éviter les appels en boucle
        if (timeline != null) timeline.stop();
        if (timerTimeline != null) timerTimeline.stop();

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/bomberman/view/menu.fxml"));
            Parent root = loader.load();
            MenuController menuController = loader.getController();
            menuController.setStage(stage);
            if (stage != null) {
                stage.setScene(new Scene(root));
            } else {
                System.err.println("Impossible de trouver la fenêtre principale !");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void checkGameOver() {
        boolean someoneDead = game.getPlayers().stream().anyMatch(p -> p.getLives() <= 0);

        if (someoneDead) {
            // Arrête les timelines si besoin
            if (timeline != null) timeline.stop();
            if (timerTimeline != null) timerTimeline.stop();
            // Reviens au menu principal
            returnToMenu();
        }
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

        // --- Avatars et compteurs de vie style Bomberman SNES ---
        double iconSize = topUiHeight * 0.7; // Taille des avatars et compteurs
        double margin = 14; // Marge latérale
        double spacing = 4; // Espace entre avatar et compteur
        double counterSize = iconSize; // Compteur carré

        // --- Joueur 1 (gauche) ---
        double p1AvatarX = margin;
        double p1AvatarY = (topUiHeight - iconSize) / 2;
        double p1CounterX = p1AvatarX + iconSize + spacing;
        double p1CounterY = p1AvatarY;

        // Affichage avatar P1
        gc.drawImage(avatarP1, p1AvatarX, p1AvatarY, iconSize, iconSize);

        // Fond du compteur de vie
        gc.setFill(Color.BLACK);
        gc.fillRoundRect(p1CounterX, p1CounterY, counterSize, counterSize, 8, 8);
        // Cadre compteur
        gc.setStroke(Color.LIGHTGRAY);
        gc.setLineWidth(2);
        gc.strokeRoundRect(p1CounterX, p1CounterY, counterSize, counterSize, 8, 8);
        // Texte vie (centré)
        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("Consolas", counterSize * 0.7));
        String p1LivesStr = String.valueOf(game.getPlayers().get(0).getLives());
        Text p1Text = new Text(p1LivesStr);
        p1Text.setFont(gc.getFont());
        double p1TextWidth = p1Text.getLayoutBounds().getWidth();
        double p1TextHeight = p1Text.getLayoutBounds().getHeight();
        double p1TextX = p1CounterX + (counterSize - p1TextWidth) / 2;
        double p1TextY = p1CounterY + counterSize - (counterSize - p1TextHeight) / 2 - 5;
        gc.fillText(p1LivesStr, p1TextX, p1TextY);

        // --- Joueur 2 (droite) ---
        if (game.getPlayers().size() > 1) {
            double p2AvatarX = canvasWidth - margin - iconSize;
            double p2AvatarY = (topUiHeight - iconSize) / 2;
            double p2CounterX = p2AvatarX - spacing - counterSize;
            double p2CounterY = p2AvatarY;

            gc.drawImage(avatarP2, p2AvatarX, p2AvatarY, iconSize, iconSize);

            gc.setFill(Color.BLACK);
            gc.fillRoundRect(p2CounterX, p2CounterY, counterSize, counterSize, 8, 8);
            gc.setStroke(Color.LIGHTGRAY);
            gc.setLineWidth(2);
            gc.strokeRoundRect(p2CounterX, p2CounterY, counterSize, counterSize, 8, 8);
            gc.setFill(Color.WHITE);
            gc.setFont(Font.font("Consolas", counterSize * 0.7));
            String p2LivesStr = String.valueOf(game.getPlayers().get(1).getLives());
            Text p2Text = new Text(p2LivesStr);
            p2Text.setFont(gc.getFont());
            double p2TextWidth = p2Text.getLayoutBounds().getWidth();
            double p2TextHeight = p2Text.getLayoutBounds().getHeight();
            double p2TextX = p2CounterX + (counterSize - p2TextWidth) / 2;
            double p2TextY = p2CounterY + counterSize - (counterSize - p2TextHeight) / 2 - 5;
            gc.fillText(p2LivesStr, p2TextX, p2TextY);
        }

        // --- Timer centré, fond élargi 1.5x ---
        String timerStr = String.format("%d:%02d", timerSeconds / 60, timerSeconds % 60);
        gc.setFont(Font.font("Consolas", topUiHeight * 0.4));
        Text timerText = new Text(timerStr);
        timerText.setFont(gc.getFont());
        double timerWidth = timerText.getLayoutBounds().getWidth();
        double timerHeight = timerText.getLayoutBounds().getHeight();

        double timerBgWidth = iconSize * 1.5; // Largeur élargie à 1.5x la hauteur
        double timerBgHeight = iconSize;
        double timerBgX = (canvasWidth - timerBgWidth) / 2;
        double timerBgY = (topUiHeight - timerBgHeight) / 2;
        double timerTextX = timerBgX + (timerBgWidth - timerWidth) / 2;
        double timerTextY = timerBgY + timerBgHeight - (timerBgHeight - timerHeight) / 2 - 5;

        gc.setFill(Color.BLACK);
        gc.fillRoundRect(timerBgX, timerBgY, timerBgWidth, timerBgHeight, 8, 8);
        gc.setStroke(Color.LIGHTGRAY);
        gc.setLineWidth(2);
        gc.strokeRoundRect(timerBgX, timerBgY, timerBgWidth, timerBgHeight, 8, 8);
        gc.setFill(Color.WHITE);
        gc.fillText(timerStr, timerTextX, timerTextY);

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
                    case INDESTRUCTIBLE -> gc.drawImage(wallIndestructibleImg, drawX, drawY, CELL_SIZE, CELL_SIZE);
                    case DESTRUCTIBLE   -> gc.drawImage(wallDestructibleImg, drawX, drawY, CELL_SIZE, CELL_SIZE);
                    case BOMB           -> {
                        // On dessine d'abord le sol, puis la bombe plus bas
                        gc.setFill(Color.rgb(0, 128, 64));
                        gc.fillRect(drawX, drawY, CELL_SIZE, CELL_SIZE);
                    }
                    case EXPLOSION -> {
                        // Fond orange
                        gc.setFill(Color.ORANGE);
                        gc.fillRect(drawX, drawY, CELL_SIZE, CELL_SIZE);

                        // Cercle jaune lumineux au centre
                        gc.setFill(Color.YELLOW);
                        gc.fillOval(drawX + CELL_SIZE * 0.2, drawY + CELL_SIZE * 0.2, CELL_SIZE * 0.6, CELL_SIZE * 0.6);

                        // Optionnel: effet blanc (flash)
                        gc.setFill(Color.rgb(255,255,255,0.4));
                        gc.fillOval(drawX + CELL_SIZE * 0.35, drawY + CELL_SIZE * 0.35, CELL_SIZE * 0.3, CELL_SIZE * 0.3);

                        // Optionnel: bordure noire
                        gc.setStroke(Color.BLACK);
                        gc.strokeRect(drawX, drawY, CELL_SIZE, CELL_SIZE);
                    }
                    default             -> {
                        gc.setFill(Color.rgb(0, 128, 64)); // Vert pelouse
                        gc.fillRect(drawX, drawY, CELL_SIZE, CELL_SIZE);
                    }
                }
                // Si ce n'est pas un mur, on dessine la grille noire
                if (grid.getCell(x, y) != com.bomberman.model.Grid.CellType.INDESTRUCTIBLE
                        && grid.getCell(x, y) != com.bomberman.model.Grid.CellType.DESTRUCTIBLE) {
                    gc.setStroke(Color.BLACK);
                    gc.strokeRect(drawX, drawY, CELL_SIZE, CELL_SIZE);
                }
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

        checkGameOver();
    }
}