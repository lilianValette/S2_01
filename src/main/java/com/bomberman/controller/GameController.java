package com.bomberman.controller;

import com.bomberman.model.Game;
import com.bomberman.model.Player;
import com.bomberman.model.Bomb;
import com.bomberman.model.Level;
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
import com.bomberman.model.Bonus;
import com.bomberman.model.ActiveBonus;

public class GameController {
    @FXML
    private Canvas gameCanvas;

    private Stage stage;

    public static final int WINDOW_WIDTH = 890;
    public static final int WINDOW_HEIGHT = 760;
    public static final int DEFAULT_CELL_SIZE = 48;
    private final int CELL_SIZE = DEFAULT_CELL_SIZE;

    private static final double BORDER_PIXEL_RATIO = 0.5;
    private static final double TOP_UI_HEIGHT_RATIO = 2.5;

    private Game game;
    private Timeline timeline;
    private Timeline timerTimeline;
    private int timerSeconds = 120;

    private static final String[] AVATAR_PATHS = {
            "/images/avatarsJoueurs/PBlanc-icon.png",
            "/images/avatarsJoueurs/PBleuCiel-icon.png",
            "/images/avatarsJoueurs/PRose-icon.png",
            "/images/avatarsJoueurs/PRouge-icon.png"
    };
    private Image[] avatarsJoueurs = new Image[4];

    private Level level;
    private int playerCount;
    private int iaCount;

    private Image wallIndestructibleImg;
    private Image wallDestructibleImg;
    private Image solImg;
    private Image bombImg;

    // Sprites joueurs (orientation)
    private Image player1Front, player1Back, player1Left, player1Right;
    private Image player2Front, player2Back, player2Left, player2Right;
    private int player1Direction = 0; // 0=bas, 1=haut, 2=gauche, 3=droite
    private int player2Direction = 0;

    public void setStage(Stage stage) {
        this.stage = stage;
        if (stage != null) {
            stage.setResizable(false);
            stage.setWidth(WINDOW_WIDTH);
            stage.setHeight(WINDOW_HEIGHT);
            stage.setMinWidth(WINDOW_WIDTH);
            stage.setMinHeight(WINDOW_HEIGHT);
            stage.setMaxWidth(WINDOW_WIDTH);
            stage.setMaxHeight(WINDOW_HEIGHT);
        }
    }

    public void setLevel(Level level) { this.level = level; }
    public void setPlayerCount(int playerCount) { this.playerCount = playerCount; }
    public void setIaCount(int iaCount) { this.iaCount = iaCount; }

    @FXML
    public void initialize() { }

    public void startGame() {
        game = new Game(15, 13, playerCount, iaCount, level);

        for (int i = 0; i < avatarsJoueurs.length; i++) {
            avatarsJoueurs[i] = new Image(getClass().getResourceAsStream(AVATAR_PATHS[i]));
        }
        wallIndestructibleImg = new Image(getClass().getResourceAsStream(level.getWallIndestructibleImagePath()));
        wallDestructibleImg   = new Image(getClass().getResourceAsStream(level.getWallDestructibleImagePath()));
        solImg                = new Image(getClass().getResourceAsStream(level.getGroundImagePath()));
        bombImg = new Image(getClass().getResourceAsStream("/images/items/bombe.png"));

        try {
            player1Front = new Image(getClass().getResourceAsStream("/images/Player/joueur_face.png"));
            player1Left  = new Image(getClass().getResourceAsStream("/images/Player/joueur_gauche.png"));
            player1Right = new Image(getClass().getResourceAsStream("/images/Player/joueur_droite.png"));
            player1Back  = new Image(getClass().getResourceAsStream("/images/Player/joueur_dos.png"));
            if (player1Front.isError()) player1Front = avatarsJoueurs[0];
            if (player1Left.isError())  player1Left  = avatarsJoueurs[0];
            if (player1Right.isError()) player1Right = avatarsJoueurs[0];
            if (player1Back.isError())  player1Back  = avatarsJoueurs[0];
        } catch (Exception e) {
            player1Front = player1Left = player1Right = player1Back = avatarsJoueurs[0];
        }
        try {
            player2Front = new Image(getClass().getResourceAsStream("/images/Player/joueur_face.png"));
            player2Left  = new Image(getClass().getResourceAsStream("/images/Player/joueur_gauche.png"));
            player2Right = new Image(getClass().getResourceAsStream("/images/Player/joueur_droite.png"));
            player2Back  = new Image(getClass().getResourceAsStream("/images/Player/joueur_dos.png"));
            if (player2Front.isError()) player2Front = avatarsJoueurs[1];
            if (player2Left.isError())  player2Left  = avatarsJoueurs[1];
            if (player2Right.isError()) player2Right = avatarsJoueurs[1];
            if (player2Back.isError())  player2Back  = avatarsJoueurs[1];
        } catch (Exception e) {
            player2Front = player2Left = player2Right = player2Back = avatarsJoueurs[1];
        }

        // Calcule la largeur de la grille pour adapter la barre supérieure et le canvas
        int gridWidth = game.getGrid().getWidth();
        int gridHeight = game.getGrid().getHeight();
        double borderPixel = CELL_SIZE * BORDER_PIXEL_RATIO;
        double topUiHeight = CELL_SIZE * TOP_UI_HEIGHT_RATIO;
        double canvasWidth = borderPixel * 2 + gridWidth * CELL_SIZE;
        double canvasHeight = topUiHeight + borderPixel * 2 + gridHeight * CELL_SIZE;

        gameCanvas.setWidth(canvasWidth);
        gameCanvas.setHeight(canvasHeight);

        // Adapter la taille de la fenêtre à la grille
        if (stage != null) {
            stage.setWidth(canvasWidth);
            stage.setHeight(canvasHeight + 40); // +40 pour la barre de titre
            stage.setMinWidth(canvasWidth);
            stage.setMinHeight(canvasHeight + 40);
            stage.setMaxWidth(canvasWidth);
            stage.setMaxHeight(canvasHeight + 40);
        }

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
            if (timerSeconds > 0) {
                timerSeconds--;
                drawGrid();
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
            menuController.setStage(stage); // Laisser le menu décider de la taille, il adapte selon l'image
            if (stage != null) {
                // La taille sera pilotée par le MenuController (plus de valeurs codées ici !)
                stage.setScene(new Scene(root));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void checkGameOver() {
        boolean someoneDead = game.getPlayers().stream().anyMatch(p -> p.getLives() <= 0);
        if (someoneDead) {
            if (timeline != null) timeline.stop();
            if (timerTimeline != null) timerTimeline.stop();
            returnToMenu();
        }
    }

    private void handleKeyPressed(KeyEvent event) {
        if (game.getPlayers().isEmpty()) return;
        Player p1 = game.getPlayers().get(0);
        Player p2 = game.getPlayers().size() > 1 ? game.getPlayers().get(1) : null;
        switch (event.getCode()) {
            case UP    -> { if (p1.isAlive()) { player1Direction = 1; game.movePlayer(p1, 0, -1); } }
            case DOWN  -> { if (p1.isAlive()) { player1Direction = 0; game.movePlayer(p1, 0,  1); } }
            case LEFT  -> { if (p1.isAlive()) { player1Direction = 2; game.movePlayer(p1, -1, 0); } }
            case RIGHT -> { if (p1.isAlive()) { player1Direction = 3; game.movePlayer(p1, 1,  0); } }
            case SPACE -> { if (p1.isAlive()) game.placeBomb(p1); }
            case Z     -> { if (p2 != null && p2.isAlive()) { player2Direction = 1; game.movePlayer(p2, 0, -1); } }
            case S     -> { if (p2 != null && p2.isAlive()) { player2Direction = 0; game.movePlayer(p2, 0, 1); } }
            case Q     -> { if (p2 != null && p2.isAlive()) { player2Direction = 2; game.movePlayer(p2, -1, 0); } }
            case D     -> { if (p2 != null && p2.isAlive()) { player2Direction = 3; game.movePlayer(p2, 1, 0); } }
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
        double canvasWidth = borderPixel * 2 + gridWidth * CELL_SIZE;
        double canvasHeight = topUiHeight + borderPixel * 2 + gridHeight * CELL_SIZE;

        // --- BARRE DU HAUT ADAPTÉE À LA GRILLE ---
        gc.setFill(Color.ORANGE);
        gc.fillRect(0, 0, canvasWidth, topUiHeight);

        int totalPlayers = game.getPlayers().size();

        // Proportions idéales (adaptées à la grille, pas à la fenêtre !)
        double desiredSpacing = topUiHeight * 0.08;
        double minSpacing = 8;
        double iconSize = topUiHeight * 0.65;
        double counterSize = topUiHeight * 0.5;
        double timerWidth = topUiHeight * 1.15;
        double timerHeight = topUiHeight * 0.85;
        double spacing = Math.max(desiredSpacing, minSpacing);

        double blocksWidth = totalPlayers * (iconSize + counterSize) + (totalPlayers + 1) * spacing + timerWidth;
        double x = (canvasWidth - blocksWidth) / 2.0;

        // Joueurs gauche
        int leftPlayers = totalPlayers <= 2 ? 1 : totalPlayers / 2;
        for (int i = 0; i < leftPlayers; i++) {
            drawPlayerBlock(gc, game.getPlayers().get(i), avatarsJoueurs[i], x, (topUiHeight - iconSize) / 2, iconSize, counterSize);
            x += iconSize + counterSize + spacing;
        }

        // Timer central
        double timerX = x;
        double timerY = (topUiHeight - timerHeight) / 2;
        gc.setFill(Color.BLACK);
        gc.fillRoundRect(timerX, timerY, timerWidth, timerHeight, 16, 16);
        gc.setStroke(Color.WHITE);
        gc.setLineWidth(2);
        gc.strokeRoundRect(timerX, timerY, timerWidth, timerHeight, 16, 16);

        String timerStr = String.format("%d:%02d", timerSeconds / 60, timerSeconds % 60);
        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("Consolas", timerHeight * 0.55));
        Text timerText = new Text(timerStr);
        timerText.setFont(gc.getFont());
        double timerTextWidth = timerText.getLayoutBounds().getWidth();
        double timerTextHeight = timerText.getLayoutBounds().getHeight();
        double timerTextX = timerX + (timerWidth - timerTextWidth) / 2;
        double timerTextY = timerY + timerHeight / 2 + timerTextHeight / 3;
        gc.fillText(timerStr, timerTextX, timerTextY);

        x += timerWidth + spacing;

        // Joueurs droite
        for (int i = leftPlayers; i < totalPlayers; i++) {
            drawPlayerBlock(gc, game.getPlayers().get(i), avatarsJoueurs[i], x, (topUiHeight - iconSize) / 2, iconSize, counterSize);
            x += iconSize + counterSize + spacing;
        }

        // --- Plateau, bonus, bombes, joueurs, etc. ---

        // Fonds noirs sur les rebords
        gc.setFill(Color.rgb(34, 34, 34));
        gc.fillRect(0, topUiHeight, canvasWidth, borderPixel);
        gc.fillRect(0, topUiHeight + borderPixel + gridHeight * CELL_SIZE, canvasWidth, borderPixel);
        gc.fillRect(0, topUiHeight, borderPixel, borderPixel + gridHeight * CELL_SIZE);
        gc.fillRect(borderPixel + gridWidth * CELL_SIZE, topUiHeight, borderPixel, borderPixel + gridHeight * CELL_SIZE);

        // Affichage des cases (aucun strokeRect, aucune ligne noire !)
        for (int y = 0; y < gridHeight; y++) {
            for (int xg = 0; xg < gridWidth; xg++) {
                double drawX = borderPixel + xg * CELL_SIZE;
                double drawY = topUiHeight + borderPixel + y * CELL_SIZE;
                switch (grid.getCell(xg, y)) {
                    case INDESTRUCTIBLE -> gc.drawImage(wallIndestructibleImg, drawX, drawY, CELL_SIZE, CELL_SIZE);
                    case DESTRUCTIBLE   -> gc.drawImage(wallDestructibleImg, drawX, drawY, CELL_SIZE, CELL_SIZE);
                    case BOMB           -> gc.drawImage(solImg, drawX, drawY, CELL_SIZE, CELL_SIZE);
                    case EXPLOSION -> {
                        gc.setFill(Color.ORANGE);
                        gc.fillRect(drawX, drawY, CELL_SIZE, CELL_SIZE);
                        gc.setFill(Color.YELLOW);
                        gc.fillOval(drawX + CELL_SIZE * 0.2, drawY + CELL_SIZE * 0.2, CELL_SIZE * 0.6, CELL_SIZE * 0.6);
                        gc.setFill(Color.rgb(255,255,255,0.4));
                        gc.fillOval(drawX + CELL_SIZE * 0.35, drawY + CELL_SIZE * 0.35, CELL_SIZE * 0.3, CELL_SIZE * 0.3);
                    }
                    default -> gc.drawImage(solImg, drawX, drawY, CELL_SIZE, CELL_SIZE);
                }
                // Plus AUCUN strokeRect ici !
            }
        }

        for (Bonus bonus : game.getBonuses()) {
            double bx = borderPixel + bonus.getX() * CELL_SIZE;
            double by = topUiHeight  + borderPixel + bonus.getY() * CELL_SIZE;
            gc.drawImage(bonus.getSprite(), bx, by, CELL_SIZE, CELL_SIZE);
        }
        for (Bomb b : game.getBombs()) {
            double bx = borderPixel + b.getX() * CELL_SIZE;
            double by = topUiHeight + borderPixel + b.getY() * CELL_SIZE;
            gc.drawImage(bombImg, bx, by, CELL_SIZE, CELL_SIZE);
        }
        for (Player p : game.getPlayers()) {
            if (p.isAlive()) {
                double px = borderPixel + p.getX() * CELL_SIZE;
                double py = topUiHeight + borderPixel + p.getY() * CELL_SIZE;
                Image currentSprite = avatarsJoueurs[p.getId() - 1];
                gc.drawImage(currentSprite, px, py, CELL_SIZE, CELL_SIZE);
            }
        }

        checkGameOver();
    }

    private void drawPlayerBlock(GraphicsContext gc, Player player, Image avatar, double x, double y, double iconSize, double counterSize) {
        gc.drawImage(avatar, x, y, iconSize, iconSize);

        double counterX = x + iconSize;
        double counterY = y + (iconSize - counterSize) / 2;
        gc.setFill(Color.BLACK);
        gc.fillRoundRect(counterX, counterY, counterSize, counterSize, 12, 12);
        gc.setStroke(Color.WHITE);
        gc.setLineWidth(2);
        gc.strokeRoundRect(counterX, counterY, counterSize, counterSize, 12, 12);

        String vieStr = String.valueOf(player.getLives());
        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("Consolas", counterSize * 0.7));
        Text text = new Text(vieStr);
        text.setFont(gc.getFont());
        double textWidth = text.getLayoutBounds().getWidth();
        double textHeight = text.getLayoutBounds().getHeight();
        double textX = counterX + (counterSize - textWidth) / 2;
        double textY = counterY + counterSize / 2 + textHeight / 3;
        gc.fillText(vieStr, textX, textY);

        if (player.isAI()) {
            gc.setFill(Color.ORANGE);
            gc.setFont(Font.font("Consolas", counterSize * 0.20));
            gc.fillText("IA", counterX + counterSize - 24, counterY + 18);
        }
    }

    public static Canvas createLevelPreviewCanvas(Level level, int cellSize) {
        int[][] preview = level.getLayout();
        int w = preview[0].length;
        int h = preview.length;

        Canvas canvas = new Canvas(w * cellSize, h * cellSize);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        Image solImg = new Image(GameController.class.getResourceAsStream(level.getGroundImagePath()));
        Image murImg = new Image(GameController.class.getResourceAsStream(level.getWallIndestructibleImagePath()));
        Image blocImg = new Image(GameController.class.getResourceAsStream(level.getWallDestructibleImagePath()));

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

        // Plus de lignes de grille !
        gc.setFill(new Color(0, 0, 0, 0.4));
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
        return canvas;
    }

    public static int getCellSize() { return DEFAULT_CELL_SIZE; }
}