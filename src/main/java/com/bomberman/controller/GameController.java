package com.bomberman.controller;

import com.bomberman.model.Game;
import com.bomberman.model.Player;
import com.bomberman.model.Bomb;
import com.bomberman.model.Theme;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.scene.image.Image;
import com.bomberman.model.Bonus;
import com.bomberman.model.ActiveBonus;
import java.io.IOException;
import java.net.URL;


public class GameController {
    @FXML
    private Canvas gameCanvas;

    @FXML
    private StackPane rootPane;


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
    private int timerSeconds = 120;
    private Image avatarP1;
    private Image avatarP2;

    private Theme theme;
    private int playerCount;
    private int iaCount;

    public void setTheme(Theme theme) { this.theme = theme; }
    public void setPlayerCount(int playerCount) { this.playerCount = playerCount; }
    public void setIaCount(int iaCount) { this.iaCount = iaCount; }

    private Image wallIndestructibleImg;
    private Image wallDestructibleImg;
    private Image bombImg;

    private boolean gameEnded = false;


    @FXML
    public void initialize() {
        // NE PAS initialiser le jeu ici !
    }

    // À appeler explicitement après avoir injecté les paramètres
    public void startGame() {
        game = new Game(15, 13, playerCount, iaCount, theme);

        avatarP1 = new Image(getClass().getResourceAsStream("/images/avatarsJoueurs/avatarBleu.png"));
        avatarP2 = new Image(getClass().getResourceAsStream("/images/avatarsJoueurs/avatarRouge.png"));
        wallIndestructibleImg = new Image(getClass().getResourceAsStream("/images/elementsMap/murIndestructible.png"));
        wallDestructibleImg = new Image(getClass().getResourceAsStream("/images/elementsMap/murDestructible.png"));
        bombImg = new Image(getClass().getResourceAsStream("/images/items/bombe.png"));

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

        timerTimeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            timerSeconds--;
            drawGrid();

            if (timerSeconds <= 0) {
                timerTimeline.stop();
                showEndGameScreen("Temps écoulé !");
            }


        }));

        timerTimeline.setCycleCount(Timeline.INDEFINITE);
        timerTimeline.play();
    }


    private void returnToMenu() {
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

    private void showEndGameScreen(String message) {
        if (timeline != null) timeline.stop();
        if (timerTimeline != null) timerTimeline.stop();

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/bomberman/view/EndGameScreen.fxml"));
            Parent root = loader.load();

            if (stage != null) {
                stage.setScene(new Scene(root));
                stage.show();
                System.out.println("Scene changée, stage : " + stage);
            } else {
                System.err.println("Stage est null, impossible de changer de scène");
            }

            EndGameScreenController controller = loader.getController();
            controller.setMessage(message);
            controller.setOnReturnCallback(() -> returnToMenu());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }





    private void checkGameOver() {
        boolean someoneDead = game.getPlayers().stream().anyMatch(p -> p.getLives() <= 0);

        if (someoneDead && !gameEnded) {
            gameEnded = true;
            Player winner = game.getPlayers().stream()
                    .filter(Player::isAlive)
                    .findFirst()
                    .orElse(null);

            String message;
            if (winner != null) {
                message = "Le joueur " + winner.getId() + " a gagné !";
            } else {
                message = "Match nul !";
            }

            showEndGameScreen(message);
        }
    }


    private void handleKeyPressed(KeyEvent event) {
        // Toujours vérifier que la liste n'est pas vide
        if (game.getPlayers().isEmpty()) return;
        Player p1 = game.getPlayers().get(0);
        Player p2 = game.getPlayers().size() > 1 ? game.getPlayers().get(1) : null;

        switch (event.getCode()) {
            case UP    -> { if (p1.isAlive()) game.movePlayer(p1, 0, -1); }
            case DOWN  -> { if (p1.isAlive()) game.movePlayer(p1, 0,  1); }
            case LEFT  -> { if (p1.isAlive()) game.movePlayer(p1, -1, 0); }
            case RIGHT -> { if (p1.isAlive()) game.movePlayer(p1, 1,  0); }
            case SPACE -> { if (p1.isAlive()) game.placeBomb(p1); }
            case Z -> { if (p2 != null && p2.isAlive()) game.movePlayer(p2, 0, -1); }
            case S -> { if (p2 != null && p2.isAlive()) game.movePlayer(p2, 0,  1); }
            case Q -> { if (p2 != null && p2.isAlive()) game.movePlayer(p2, -1, 0); }
            case D -> { if (p2 != null && p2.isAlive()) game.movePlayer(p2, 1,  0); }
            case SHIFT -> { if (p2 != null && p2.isAlive()) game.placeBomb(p2); }
        }
        drawGrid();
    }

    private void drawGrid() {
        if (game == null || game.getPlayers().isEmpty()) return;

        GraphicsContext gc = gameCanvas.getGraphicsContext2D();
        var grid = game.getGrid();

        int gridWidth = grid.getWidth();
        int gridHeight = grid.getHeight();
        double borderPixel = CELL_SIZE * BORDER_PIXEL_RATIO;
        double topUiHeight = CELL_SIZE * TOP_UI_HEIGHT_RATIO;
        double canvasWidth = borderPixel + gridWidth * CELL_SIZE + borderPixel;

        gc.clearRect(0, 0, gameCanvas.getWidth(), gameCanvas.getHeight());

        gc.setFill(Color.ORANGE);
        gc.fillRect(0, 0, gameCanvas.getWidth(), topUiHeight);

        double iconSize = topUiHeight * 0.7;
        double margin = 14;
        double spacing = 4;
        double counterSize = iconSize;

        double p1AvatarX = margin;
        double p1AvatarY = (topUiHeight - iconSize) / 2;
        double p1CounterX = p1AvatarX + iconSize + spacing;
        double p1CounterY = p1AvatarY;

        gc.drawImage(avatarP1, p1AvatarX, p1AvatarY, iconSize, iconSize);

        gc.setFill(Color.BLACK);
        gc.fillRoundRect(p1CounterX, p1CounterY, counterSize, counterSize, 8, 8);
        gc.setStroke(Color.LIGHTGRAY);
        gc.setLineWidth(2);
        gc.strokeRoundRect(p1CounterX, p1CounterY, counterSize, counterSize, 8, 8);

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

        double p2CounterX = 0;
        double p2CounterY = 0;
        // --- Joueur 2 (droite) ---
        if (game.getPlayers().size() > 1) {
            double p2AvatarX = canvasWidth - margin - iconSize;
            double p2AvatarY = (topUiHeight - iconSize) / 2;
            p2CounterX = p2AvatarX - spacing - counterSize;
            p2CounterY = p2AvatarY;

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

        // Afficher le temps restant du bonus FLAME pour chaque joueur
        for (Player p : game.getPlayers()) {
            double textX;
            double baseY;
            int bonusIndex = 0;

            if (p.getId() == 1) {
                textX = margin + iconSize + spacing + counterSize + 8;
                baseY = topUiHeight * 0.6;
            } else {
                textX = canvasWidth - margin - iconSize - spacing - counterSize - 75;
                baseY = topUiHeight * 0.6;
            }

            for (ActiveBonus ab : p.getActiveBonuses()) {
                String suffix = switch (ab.getType()) {
                    case FLAME -> " F";
                    case JACKET -> " J";
                    case LIFE -> " L";
                };

                String timeStr = ab.getSecondsRemaining() + "s" + suffix;

                gc.setFill(Color.WHITE);
                gc.setFont(Font.font("Consolas", iconSize * 0.4));
                double textY = baseY + bonusIndex * 18;
                gc.fillText(timeStr, textX, textY);
                bonusIndex++;
            }
        }


        // --- Timer centré, fond élargi 1.5x ---
        String timerStr = String.format("%d:%02d", timerSeconds / 60, timerSeconds % 60);
        gc.setFont(Font.font("Consolas", topUiHeight * 0.4));
        Text timerText = new Text(timerStr);
        timerText.setFont(gc.getFont());
        double timerWidth = timerText.getLayoutBounds().getWidth();
        double timerHeight = timerText.getLayoutBounds().getHeight();

        double timerBgWidth = iconSize * 1.5;
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

        gc.setFill(Color.rgb(34, 34, 34));
        gc.fillRect(0, topUiHeight, canvasWidth, borderPixel);
        gc.fillRect(0, topUiHeight + borderPixel + gridHeight * CELL_SIZE, canvasWidth, borderPixel);
        gc.fillRect(0, topUiHeight, borderPixel, borderPixel + gridHeight * CELL_SIZE);
        gc.fillRect(borderPixel + gridWidth * CELL_SIZE, topUiHeight, borderPixel, borderPixel + gridHeight * CELL_SIZE);

        for (int y = 0; y < gridHeight; y++) {
            for (int x = 0; x < gridWidth; x++) {
                double drawX = borderPixel + x * CELL_SIZE;
                double drawY = topUiHeight + borderPixel + y * CELL_SIZE;

                switch (grid.getCell(x, y)) {
                    case INDESTRUCTIBLE -> gc.drawImage(wallIndestructibleImg, drawX, drawY, CELL_SIZE, CELL_SIZE);
                    case DESTRUCTIBLE   -> gc.drawImage(wallDestructibleImg, drawX, drawY, CELL_SIZE, CELL_SIZE);
                    case BOMB           -> {
                        gc.setFill(Color.rgb(0, 128, 64));
                        gc.fillRect(drawX, drawY, CELL_SIZE, CELL_SIZE);
                    }
                    case EXPLOSION -> {
                        gc.setFill(Color.ORANGE);
                        gc.fillRect(drawX, drawY, CELL_SIZE, CELL_SIZE);

                        gc.setFill(Color.YELLOW);
                        gc.fillOval(drawX + CELL_SIZE * 0.2, drawY + CELL_SIZE * 0.2, CELL_SIZE * 0.6, CELL_SIZE * 0.6);

                        gc.setFill(Color.rgb(255,255,255,0.4));
                        gc.fillOval(drawX + CELL_SIZE * 0.35, drawY + CELL_SIZE * 0.35, CELL_SIZE * 0.3, CELL_SIZE * 0.3);

                        gc.setStroke(Color.BLACK);
                        gc.strokeRect(drawX, drawY, CELL_SIZE, CELL_SIZE);
                    }
                    default             -> {
                        gc.setFill(Color.rgb(0, 128, 64));
                        gc.fillRect(drawX, drawY, CELL_SIZE, CELL_SIZE);
                    }
                }
                if (grid.getCell(x, y) != com.bomberman.model.Grid.CellType.INDESTRUCTIBLE
                        && grid.getCell(x, y) != com.bomberman.model.Grid.CellType.DESTRUCTIBLE) {
                    gc.setStroke(Color.BLACK);
                    gc.strokeRect(drawX, drawY, CELL_SIZE, CELL_SIZE);
                }
            }
        }
        // dessiner chaque bonus
        for (Bonus bonus : game.getBonuses()) {
            double bx = borderPixel + bonus.getX() * CELL_SIZE;
            double by = topUiHeight  + borderPixel + bonus.getY() * CELL_SIZE;
            gc.drawImage(bonus.getSprite(), bx, by, CELL_SIZE, CELL_SIZE);
        }

        // --- Dessine les bombes avec l'image ---
        for (Bomb b : game.getBombs()) {
            double bx = borderPixel + b.getX() * CELL_SIZE;
            double by = topUiHeight + borderPixel + b.getY() * CELL_SIZE;
            gc.drawImage(bombImg, bx, by, CELL_SIZE, CELL_SIZE);
        }
        for (Player p : game.getPlayers()) {
            if (p.isAlive()) {
                double px = borderPixel + p.getX() * CELL_SIZE + 4;
                double py = topUiHeight + borderPixel + p.getY() * CELL_SIZE + 4;
                gc.setFill(p.getId() == 1 ? Color.BLUE : Color.RED);
                gc.fillOval(px, py, CELL_SIZE - 8, CELL_SIZE - 8);
            }
        }

        checkGameOver();
    }

    public static Canvas createThemePreviewCanvas(Theme theme, int cellSize) {
        int[][] preview = theme.getLayout();
        int w = preview[0].length;
        int h = preview.length;

        Canvas canvas = new Canvas(w * cellSize, h * cellSize);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        Image solImg = new Image(GameController.class.getResourceAsStream(theme.getSolImagePath()));
        Image murImg = new Image(GameController.class.getResourceAsStream(theme.getMurImagePath()));
        Image blocImg = new Image(GameController.class.getResourceAsStream(theme.getDestructibleImagePath()));

        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                double px = x * cellSize;
                double py = y * cellSize;
                switch (preview[y][x]) {
                    case 1 -> gc.drawImage(murImg, px, py, cellSize, cellSize);
                    case 2 -> gc.drawImage(blocImg, px, py, cellSize, cellSize);
                    default -> gc.drawImage(solImg, px, py, cellSize, cellSize);
                }
            }
        }
        gc.setStroke(javafx.scene.paint.Color.rgb(0,0,0,0.5));
        for (int y = 0; y <= h; y++)
            gc.strokeLine(0, y*cellSize, w*cellSize, y*cellSize);
        for (int x = 0; x <= w; x++)
            gc.strokeLine(x*cellSize, 0, x*cellSize, h*cellSize);

        // Ajoute un voile noir semi-transparent par-dessus
        gc.setFill(new Color(0, 0, 0, 0.4)); // 50% de transparence
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());


        return canvas;
    }
}