package com.bomberman.controller;

import com.bomberman.model.Game;
import com.bomberman.model.Player;
import com.bomberman.model.Bomb;
import com.bomberman.model.Level;
import com.bomberman.model.Bonus;
import com.bomberman.model.AIDifficulty;
import com.bomberman.model.GameSettings;
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

import java.io.InputStream;

/**
 * Contrôleur principal du jeu Bomberman.
 * Gère l'affichage, les entrées clavier et la logique de jeu,
 * y compris la direction des sprites IA & joueurs, et le timer.
 */
public class GameController {
    @FXML
    private Canvas gameCanvas;

    private Stage stage;

    // Constantes d'affichage
    public static final int DEFAULT_CELL_SIZE = 48;
    private final int CELL_SIZE = DEFAULT_CELL_SIZE;
    private static final double BORDER_PIXEL_RATIO = 0.5;
    private static final double TOP_UI_HEIGHT_RATIO = 2.5;

    // Données du jeu
    private Game game;
    private Timeline gameTimeline;
    private Timeline timerTimeline;
    private int timerSeconds = 180; // 3 minutes

    // Ressources graphiques
    private static final String[] AVATAR_PATHS = {
            "/images/avatarsJoueurs/PBlanc-icon.png",
            "/images/avatarsJoueurs/PBleuCiel-icon.png",
            "/images/avatarsJoueurs/PRose-icon.png",
            "/images/avatarsJoueurs/PRouge-icon.png"
    };
    private final Image[] avatarsJoueurs = new Image[4];

    // Sprites directionnels pour chaque joueur : [playerIndex][direction]
    // Directions : 0=bas, 1=haut, 2=gauche, 3=droite
    private static final String[][] PLAYER_SPRITE_PATHS = {
            {
                    "/images/Player/PBlanc/PBlanc-face.png",
                    "/images/Player/PBlanc/PBlanc-dos.png",
                    "/images/Player/PBlanc/PBlanc-gauche.png",
                    "/images/Player/PBlanc/PBlanc-droite.png"
            },
            {
                    "/images/Player/PBleuCiel/PBleuCiel-face.png",
                    "/images/Player/PBleuCiel/PBleuCiel-dos.png",
                    "/images/Player/PBleuCiel/PBleuCiel-gauche.png",
                    "/images/Player/PBleuCiel/PBleuCiel-droite.png"
            },
            {
                    "/images/Player/PRose/PRose-face.png",
                    "/images/Player/PRose/PRose-dos.png",
                    "/images/Player/PRose/PRose-gauche.png",
                    "/images/Player/PRose/PRose-droite.png"
            },
            {
                    "/images/Player/PRouge/PRouge-face.png",
                    "/images/Player/PRouge/PRouge-dos.png",
                    "/images/Player/PRouge/PRouge-gauche.png",
                    "/images/Player/PRouge/PRouge-droite.png"
            }
    };
    private final Image[][] playerSprites = new Image[4][4];

    // Pour la direction d'affichage de chaque joueur/IA (0=bas, 1=haut, 2=gauche, 3=droite)
    // Indexé sur la position dans game.getPlayers()
    private int[] playerDirections = new int[4];

    private Level level;
    private int playerCount;
    private int iaCount;
    private AIDifficulty aiDifficulty;

    private Image wallIndestructibleImg;
    private Image wallDestructibleImg;
    private Image solImg;
    private Image bombImg;

    public void setStage(Stage stage) { this.stage = stage; }
    public void setLevel(Level level) { this.level = level; }
    public void setPlayerCount(int playerCount) { this.playerCount = playerCount; }
    public void setIaCount(int iaCount) { this.iaCount = iaCount; }
    public void setAIDifficulty(AIDifficulty aiDifficulty) { this.aiDifficulty = aiDifficulty; }

    @FXML
    public void initialize() {}

    public void startGame() {
        // 1. Initialisation du modèle avec la difficulté IA
        AIDifficulty difficulty = (aiDifficulty != null) ? aiDifficulty : GameSettings.getSelectedAIDifficulty();
        game = new Game(15, 13, playerCount, iaCount, level, difficulty);

        // 2. Chargement des ressources
        for (int i = 0; i < avatarsJoueurs.length; i++) {
            avatarsJoueurs[i] = safeImageFromResource(AVATAR_PATHS[i]);
        }
        for (int i = 0; i < 4; i++) {
            for (int d = 0; d < 4; d++) {
                try {
                    playerSprites[i][d] = safeImageFromResource(PLAYER_SPRITE_PATHS[i][d]);
                    if (playerSprites[i][d].isError()) playerSprites[i][d] = avatarsJoueurs[i];
                } catch (Exception e) {
                    playerSprites[i][d] = avatarsJoueurs[i];
                }
            }
        }
        wallIndestructibleImg = safeImageFromResource(level.getWallIndestructibleImagePath());
        wallDestructibleImg   = safeImageFromResource(level.getWallDestructibleImagePath());
        solImg                = safeImageFromResource(level.getGroundImagePath());
        bombImg               = safeImageFromResource("/images/items/bombe.png");

        // 3. Taille du canvas/fenêtre
        int gridWidth = game.getGrid().getWidth();
        int gridHeight = game.getGrid().getHeight();
        double borderPixel = CELL_SIZE * BORDER_PIXEL_RATIO;
        double topUiHeight = CELL_SIZE * TOP_UI_HEIGHT_RATIO;
        double canvasWidth = borderPixel * 2 + gridWidth * CELL_SIZE;
        double canvasHeight = topUiHeight + borderPixel * 2 + gridHeight * CELL_SIZE;
        gameCanvas.setWidth(canvasWidth);
        gameCanvas.setHeight(canvasHeight);
        if (stage != null) {
            stage.setWidth(canvasWidth);
            stage.setHeight(canvasHeight + 40);
            stage.setMinWidth(canvasWidth);
            stage.setMinHeight(canvasHeight + 40);
            stage.setMaxWidth(canvasWidth);
            stage.setMaxHeight(canvasHeight + 40);
        }

        // 4. Directions initiales : tous vers le bas
        playerDirections = new int[game.getPlayers().size()];
        for (int i = 0; i < playerDirections.length; i++) playerDirections[i] = 0;

        // 5. Premier affichage
        drawGrid();

        // 6. Ecoute clavier
        gameCanvas.setFocusTraversable(true);
        gameCanvas.setOnKeyPressed(this::handleKeyPressed);

        // 7. Game tick (IA, bombes, etc) avec gestion de direction des IA
        if (gameTimeline != null) gameTimeline.stop();
        gameTimeline = new Timeline(new KeyFrame(Duration.seconds(0.2), e -> updateIAAndGame()));
        gameTimeline.setCycleCount(Timeline.INDEFINITE);
        gameTimeline.play();

        // 8. Timer décompte (3 minutes)
        if (timerTimeline != null) timerTimeline.stop();
        timerSeconds = 180;
        timerTimeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            timerSeconds--;
            if (timerSeconds <= 0) {
                timerTimeline.stop();
                gameTimeline.stop();
                returnToMenu();
            }
            drawGrid();
        }));
        timerTimeline.setCycleCount(Timeline.INDEFINITE);
        timerTimeline.play();
    }

    /**
     * Tick du jeu : met à jour IA, bombes et directions des IA.
     */
    private void updateIAAndGame() {
        // Mémorise les positions avant
        int nbPlayers = game.getPlayers().size();
        int[] prevX = new int[nbPlayers];
        int[] prevY = new int[nbPlayers];
        for (int i = 0; i < nbPlayers; i++) {
            Player p = game.getPlayers().get(i);
            prevX[i] = p.getX();
            prevY[i] = p.getY();
        }

        // Tick IA et bombes
        game.updateAIs();
        game.updateBombs();

        // Met à jour direction des IA (pour tous les joueurs non humains)
        for (int i = 0; i < nbPlayers; i++) {
            Player p = game.getPlayers().get(i);
            if (!p.isHuman()) {
                int dx = p.getX() - prevX[i];
                int dy = p.getY() - prevY[i];
                if      (dx ==  1) playerDirections[i] = 3; // droite
                else if (dx == -1) playerDirections[i] = 2; // gauche
                else if (dy ==  1) playerDirections[i] = 0; // bas
                else if (dy == -1) playerDirections[i] = 1; // haut
                // sinon : direction inchangée
            }
        }
        drawGrid();
    }

    /** Retour au menu principal */
    private void returnToMenu() {
        if (gameTimeline != null) gameTimeline.stop();
        if (timerTimeline != null) timerTimeline.stop();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/bomberman/view/menu.fxml"));
            Parent root = loader.load();
            MenuController menuController = loader.getController();
            menuController.setStage(stage);
            if (stage != null) stage.setScene(new Scene(root));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /** Fin de partie si un joueur est mort */
    private void checkGameOver() {
        boolean someoneDead = game.getPlayers().stream().anyMatch(p -> p.getLives() <= 0);
        if (someoneDead) {
            if (gameTimeline != null) gameTimeline.stop();
            if (timerTimeline != null) timerTimeline.stop();
            returnToMenu();
        }
    }

    /** Entrées clavier joueurs humains : gère aussi leur direction */
    private void handleKeyPressed(KeyEvent event) {
        if (game.getPlayers().isEmpty()) return;
        for (int idx = 0; idx < game.getPlayers().size(); idx++) {
            Player p = game.getPlayers().get(idx);
            if (!p.isAlive() || !p.isHuman()) continue;
            switch (idx) {
                case 0 -> { // Joueur 1 : flèches + espace
                    switch (event.getCode()) {
                        case UP    -> { playerDirections[0] = 1; game.movePlayer(p, 0, -1); }
                        case DOWN  -> { playerDirections[0] = 0; game.movePlayer(p, 0, 1); }
                        case LEFT  -> { playerDirections[0] = 2; game.movePlayer(p, -1, 0); }
                        case RIGHT -> { playerDirections[0] = 3; game.movePlayer(p, 1, 0); }
                        case SPACE -> game.placeBomb(p);
                    }
                }
                case 1 -> { // Joueur 2 : ZQSD + shift
                    switch (event.getCode()) {
                        case Z     -> { playerDirections[1] = 1; game.movePlayer(p, 0, -1); }
                        case S     -> { playerDirections[1] = 0; game.movePlayer(p, 0, 1); }
                        case Q     -> { playerDirections[1] = 2; game.movePlayer(p, -1, 0); }
                        case D     -> { playerDirections[1] = 3; game.movePlayer(p, 1, 0); }
                        case SHIFT -> game.placeBomb(p);
                    }
                }
                // Ajoutez ici les mappings pour joueurs 3/4 si besoin
            }
        }
        drawGrid();
    }

    /** Affichage principal du plateau, des joueurs, du timer, etc. */
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

        // --- Barre du haut ---
        gc.setFill(Color.ORANGE);
        gc.fillRect(0, 0, canvasWidth, topUiHeight);

        int totalPlayers = game.getPlayers().size();
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

        String timerStr = String.format("%d:%02d", Math.max(timerSeconds, 0) / 60, Math.max(timerSeconds, 0) % 60);
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

        // --- Plateau, bonus, bombes, joueurs, etc ---
        // Fonds noirs sur les rebords
        gc.setFill(Color.rgb(34, 34, 34));
        gc.fillRect(0, topUiHeight, canvasWidth, borderPixel);
        gc.fillRect(0, topUiHeight + borderPixel + gridHeight * CELL_SIZE, canvasWidth, borderPixel);
        gc.fillRect(0, topUiHeight, borderPixel, borderPixel + gridHeight * CELL_SIZE);
        gc.fillRect(borderPixel + gridWidth * CELL_SIZE, topUiHeight, borderPixel, borderPixel + gridHeight * CELL_SIZE);

        // Affichage des cases
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
            }
        }

        // Affichage des bonus
        for (Bonus bonus : game.getBonuses()) {
            double bx = borderPixel + bonus.getX() * CELL_SIZE;
            double by = topUiHeight  + borderPixel + bonus.getY() * CELL_SIZE;
            gc.drawImage(bonus.getSprite(), bx, by, CELL_SIZE, CELL_SIZE);
        }
        // Affichage des bombes
        for (Bomb b : game.getBombs()) {
            double bx = borderPixel + b.getX() * CELL_SIZE;
            double by = topUiHeight + borderPixel + b.getY() * CELL_SIZE;
            gc.drawImage(bombImg, bx, by, CELL_SIZE, CELL_SIZE);
        }
        // Affichage des joueurs (humains ET IA) avec sprite directionnel
        for (int idx = 0; idx < game.getPlayers().size(); idx++) {
            Player p = game.getPlayers().get(idx);
            if (p.isAlive()) {
                double px = borderPixel + p.getX() * CELL_SIZE;
                double py = topUiHeight + borderPixel + p.getY() * CELL_SIZE;
                int direction = playerDirections[idx];
                Image currentSprite = playerSprites[idx][direction];
                gc.drawImage(currentSprite, px, py, CELL_SIZE, CELL_SIZE);
            }
        }

        checkGameOver();
    }

    /** Affiche l'avatar d'un joueur, ses vies, et s'il est IA. */
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

    /** DRY : charge une image depuis un chemin ressource ou disque, toujours chemin relatif ressource. */
    public static Image safeImageFromResource(String path) {
        String fixedPath = path;
        if (fixedPath != null && (fixedPath.contains(":\\") || fixedPath.contains(":/") || fixedPath.startsWith("\\") || fixedPath.startsWith("/"))) {
            // On cherche le dossier "images" dans le chemin...
            int idx = fixedPath.lastIndexOf("images");
            if (idx != -1) {
                fixedPath = "/" + fixedPath.substring(idx).replace("\\", "/");
            }
        }
        InputStream is = GameController.class.getResourceAsStream(fixedPath);
        if (is == null) {
            throw new IllegalArgumentException("Image not found in resources: " + fixedPath + " (original: " + path + ")");
        }
        return new Image(is);
    }

    /** Génère un canvas de preview pour l'écran de sélection de niveau. */
    public static Canvas createLevelPreviewCanvas(Level level, int cellSize) {
        int[][] preview = level.getLayout();
        int w = preview[0].length;
        int h = preview.length;

        Canvas canvas = new Canvas(w * cellSize, h * cellSize);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        Image solImg = safeImageFromResource(level.getGroundImagePath());
        Image murImg = safeImageFromResource(level.getWallIndestructibleImagePath());
        Image blocImg = safeImageFromResource(level.getWallDestructibleImagePath());

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
        gc.setFill(new Color(0, 0, 0, 0.4));
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
        return canvas;
    }

    public static int getCellSize() { return DEFAULT_CELL_SIZE; }
}