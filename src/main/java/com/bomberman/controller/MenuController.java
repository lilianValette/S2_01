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
    @FXML private Button accountButton;
    @FXML private Button settingsButton;
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

        playButton.setOnAction(e -> startGameSetup());
        accountButton.setOnAction(e -> onAccount());
        settingsButton.setOnAction(e -> onSettings());
        quitButton.setOnAction(e -> System.exit(0));
    }

    private void startAnimations(double paneWidth) {
        // --- Dirigeable (inchangé) ---
        planeImage.setScaleX(1);
        planeImage.setTranslateY(30);
        double planeWidth = planeImage.getFitWidth();
        double planeStartX = paneWidth / 2 + planeWidth + 150;
        double planeEndX   = -paneWidth / 2 - planeWidth;
        double planeDuration = 14.0;
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

        // --- Ballon : déplacement sinusoidal sur Y + translation sur X ---
        balloonImage.setScaleX(1);
        double balloonWidth = balloonImage.getFitWidth();
        double balloonStartX = -paneWidth / 2 - (balloonWidth + 300);
        double balloonEndX   = paneWidth / 2 + (balloonWidth - 300);
        double balloonDuration = 22.0;
        double balloonTotalDistance = balloonEndX - balloonStartX;
        double balloonSpeedPerFrame = balloonTotalDistance / (balloonDuration * 60.0);

        // Valeurs pour l'oscillation verticale
        double balloonBaseY = 160;          // Position Y de base du ballon
        double oscillationAmplitude = 18.0; // Hauteur max de l'oscillation (pixels)
        double oscillationFrequency = 0.7;  // Fréquence (oscillations/seconde)

        balloonImage.setTranslateX(balloonStartX);

        if (balloonTimeline != null) balloonTimeline.stop();
        final double[] balloonFrame = {0};
        balloonTimeline = new Timeline(new KeyFrame(Duration.millis(1000.0/60.0), e -> {
            double currentX = balloonImage.getTranslateX();
            currentX += balloonSpeedPerFrame;
            if (currentX >= balloonEndX) {
                currentX = balloonStartX;
                balloonFrame[0] = 0; // reset oscillation phase
            }
            balloonImage.setTranslateX(currentX);

            // Oscillation verticale
            double t = balloonFrame[0] / 60.0; // temps écoulé en secondes
            double offsetY = balloonBaseY + oscillationAmplitude * Math.sin(2 * Math.PI * oscillationFrequency * t);
            balloonImage.setTranslateY(offsetY);

            balloonFrame[0]++;
        }));
        balloonTimeline.setCycleCount(Timeline.INDEFINITE);
        balloonTimeline.play();
    }

    @FXML
    private void startGameSetup() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/bomberman/view/game-setup.fxml"));
            Parent root = loader.load();
            GameSetupController controller = loader.getController();
            controller.setStage(stage);
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Navigation vers la page des comptes
    private void onAccount() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/bomberman/view/account.fxml"));
            Parent root = loader.load();
            AccountControler accountController = loader.getController();
            accountController.setStage(stage);
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Erreur lors du chargement de la page des comptes : " + e.getMessage());
        }
    }

    private void onSettings() {
        // À implémenter plus tard
        System.out.println("Settings button clicked");
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