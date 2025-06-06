package com.bomberman.controller;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

public class MenuController {
    @FXML private Button playButton;
    @FXML private Button quitButton;

    @FXML private ImageView backgroundImage;
    @FXML private ImageView planeImage;
    @FXML private ImageView balloonImage;
    @FXML private ImageView logoImage;
    @FXML private StackPane rootPane;

    private Stage stage;
    private Timeline planeTimeline;
    private Timeline balloonTimeline;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    public void initialize() {
        backgroundImage.setImage(loadImage("/images/menu/Bomber_fond.jpg"));
        planeImage.setImage(loadImage("/images/menu/Bomber_plane-removebg-preview.png"));
        balloonImage.setImage(loadImage("/images/menu/Bomber_balloon-removebg-preview.png"));
        logoImage.setImage(loadImage("/images/menu/logo.gif"));

        planeImage.setFitWidth(400); // taille dirigeable
        balloonImage.setFitWidth(140); // taille ballon

        rootPane.widthProperty().addListener((obs, oldVal, newVal) -> {
            startAnimations(newVal.doubleValue());
        });

        playButton.setOnAction(e -> startGame());
        quitButton.setOnAction(e -> System.exit(0));
    }

    private void startAnimations(double paneWidth) {
        // --- Dirigeable ---
        planeImage.setScaleX(1);
        planeImage.setTranslateY(30);
        double planeWidth = planeImage.getFitWidth();
        double planeStartX = paneWidth / 2 + planeWidth + 150;
        double planeEndX   = -paneWidth / 2 - planeWidth;
        double planeDuration = 14.0; // secondes pour traverser l'écran

        // Calcul du déplacement par frame (à 60 FPS)
        double planeTotalDistance = planeStartX - planeEndX;
        double planeSpeedPerFrame = planeTotalDistance / (planeDuration * 60.0);

        planeImage.setTranslateX(planeStartX);

        if (planeTimeline != null) planeTimeline.stop();
        planeTimeline = new Timeline(new KeyFrame(Duration.millis(1000.0/60.0), e -> {
            double currentX = planeImage.getTranslateX();
            currentX -= planeSpeedPerFrame;
            if (currentX <= planeEndX) {
                currentX = planeStartX;
            }
            planeImage.setTranslateX(currentX);
        }));
        planeTimeline.setCycleCount(Timeline.INDEFINITE);
        planeTimeline.play();

        // --- Ballon ---
        balloonImage.setScaleX(1);
        balloonImage.setTranslateY(160);
        double balloonWidth = balloonImage.getFitWidth();
        double balloonStartX = -paneWidth / 2 - (balloonWidth + 300);
        double balloonEndX   = paneWidth / 2 + (balloonWidth - 300);
        double balloonDuration = 22.0;

        double balloonTotalDistance = balloonEndX - balloonStartX;
        double balloonSpeedPerFrame = balloonTotalDistance / (balloonDuration * 60.0);

        balloonImage.setTranslateX(balloonStartX);

        if (balloonTimeline != null) balloonTimeline.stop();
        balloonTimeline = new Timeline(new KeyFrame(Duration.millis(1000.0/60.0), e -> {
            double currentX = balloonImage.getTranslateX();
            currentX += balloonSpeedPerFrame;
            if (currentX >= balloonEndX) {
                currentX = balloonStartX;
            }
            balloonImage.setTranslateX(currentX);
        }));
        balloonTimeline.setCycleCount(Timeline.INDEFINITE);
        balloonTimeline.play();
    }

    private void startGame() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/bomberman/view/game-view.fxml"));
            Parent root = loader.load();
            GameController gameController = loader.getController();
            gameController.setStage(stage);
            stage.setScene(new Scene(root));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private Image loadImage(String resourcePath) {
        java.net.URL url = getClass().getResource(resourcePath);
        if (url == null) {
            System.err.println("Image non trouvée : " + resourcePath);
            return null;
        }
        return new Image(url.toExternalForm());
    }
}