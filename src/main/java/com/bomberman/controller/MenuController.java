package com.bomberman.controller;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * Contrôleur du menu principal du jeu Bomberman.
 * Gère l'affichage du menu, les animations et la navigation vers l'écran de configuration de partie.
 */
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

    /**
     * Définit le stage principal et adapte la fenêtre à la taille de l'image de fond.
     * @param stage Stage principal
     */
    public void setStage(Stage stage) {
        this.stage = stage;
        adaptStageToBackgroundImage();
    }

    /**
     * Initialise les composants graphiques et les événements du menu.
     */
    @FXML
    public void initialize() {
        backgroundImage.setImage(loadImage("/images/menu/Bomber_fond.jpg"));
        planeImage.setImage(loadImage("/images/menu/Bomber_plane-removebg-preview.png"));
        balloonImage.setImage(loadImage("/images/menu/Bomber_balloon-removebg-preview.png"));
        logoImage.setImage(loadImage("/images/menu/logo.gif"));

        planeImage.setFitWidth(400);    // Taille du dirigeable
        balloonImage.setFitWidth(140);  // Taille du ballon

        // Adapter la fenêtre à la taille de l’image de fond si elle change
        backgroundImage.imageProperty().addListener((obs, oldImg, newImg) -> adaptStageToBackgroundImage());
        adaptStageToBackgroundImage();

        playButton.setOnAction(e -> startGameSetup());
        accountButton.setOnAction(e -> onAccount());
        settingsButton.setOnAction(e -> onSettings());
        quitButton.setOnAction(e -> System.exit(0));

        // Lancer les animations lors du redimensionnement
        rootPane.widthProperty().addListener((obs, oldVal, newVal) -> startAnimations(newVal.doubleValue()));
    }

    /**
     * Ajuste la fenêtre à la taille de l’ImageView du fond (définie en FXML).
     */
    private void adaptStageToBackgroundImage() {
        if (stage != null && backgroundImage != null) {
            double w = backgroundImage.getFitWidth();
            double h = backgroundImage.getFitHeight();
            if (w > 0 && h > 0) {
                stage.setMinWidth(w);
                stage.setMinHeight(h);
                stage.setMaxWidth(w);
                stage.setMaxHeight(h);
                stage.setWidth(w);
                stage.setHeight(h);
                stage.setResizable(false);
            }
        }
    }

    /**
     * Démarre les animations des éléments graphiques du menu.
     * @param paneWidth largeur du panel principal
     */
    private void startAnimations(double paneWidth) {
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

        balloonImage.setScaleX(1);
        double balloonWidth = balloonImage.getFitWidth();
        double balloonStartX = -paneWidth / 2 - (balloonWidth + 300);
        double balloonEndX   = paneWidth / 2 + (balloonWidth - 300);
        double balloonDuration = 22.0;
        double balloonTotalDistance = balloonEndX - balloonStartX;
        double balloonSpeedPerFrame = balloonTotalDistance / (balloonDuration * 60.0);

        double balloonBaseY = 160;
        double oscillationAmplitude = 18.0;
        double oscillationFrequency = 0.7;

        balloonImage.setTranslateX(balloonStartX);

        if (balloonTimeline != null) balloonTimeline.stop();
        final double[] balloonFrame = {0};
        balloonTimeline = new Timeline(new KeyFrame(Duration.millis(1000.0/60.0), e -> {
            double currentX = balloonImage.getTranslateX();
            currentX += balloonSpeedPerFrame;
            if (currentX >= balloonEndX) {
                currentX = balloonStartX;
                balloonFrame[0] = 0;
            }
            balloonImage.setTranslateX(currentX);
            double t = balloonFrame[0] / 60.0;
            double offsetY = balloonBaseY + oscillationAmplitude * Math.sin(2 * Math.PI * oscillationFrequency * t);
            balloonImage.setTranslateY(offsetY);
            balloonFrame[0]++;
        }));
        balloonTimeline.setCycleCount(Timeline.INDEFINITE);
        balloonTimeline.play();
    }

    /**
     * Passe à l'écran de configuration de partie en réutilisant la même fenêtre
     * et adapte sa taille en fonction du contenu affiché par GameSetupController.
     */
    @FXML
    private void startGameSetup() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/bomberman/view/game-setup.fxml"));
            Parent root = loader.load();
            GameSetupController controller = loader.getController();

            // Passe le même stage à GameSetupController pour permettre l'adaptation dynamique
            controller.setStage(stage);

            // Remplace la scène actuelle par celle du setup
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            e.printStackTrace();
            // Optionnel : afficher une pop-up d'erreur à l'utilisateur
        }
    }

    /**
     * Action sur le bouton "Compte".
     */
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

    /**
     * Action sur le bouton "Paramètres".
     */
    private void onSettings() {
        System.out.println("Settings button clicked");
    }

    /**
     * Charge une image à partir des ressources.
     * @param resourcePath chemin vers la ressource
     * @return l'image chargée, ou null si introuvable
     */
    private Image loadImage(String resourcePath) {
        java.net.URL url = getClass().getResource(resourcePath);
        if (url == null) {
            System.err.println("Image non trouvée : " + resourcePath);
            return null;
        }
        return new Image(url.toExternalForm());
    }
}