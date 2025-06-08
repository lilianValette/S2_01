package com.bomberman.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class EndGameScreenController {
    @FXML
    private Label messageLabel;

    private Runnable onReturnCallback;

    @FXML
    private ImageView backgroundImage;

    @FXML
    private ImageView ballonImage;

    @FXML
    private ImageView planeImage;

    @FXML
    public void initialize() {
        // Charge l'image depuis le dossier resources
        Image img = new Image(getClass().getResourceAsStream("/images/menu/Bomber_fond.jpg"));
        backgroundImage.setImage(img);
        Image img2 = new Image(getClass().getResourceAsStream("/images/menu/Bomber_balloon-removebg-preview.png"));
        ballonImage.setImage(img2);
        Image img3 = new Image(getClass().getResourceAsStream("/images/menu/Bomber_plane-removebg-preview.png"));
        planeImage.setImage(img3);

    }


    public void setMessage(String message) {
        messageLabel.setText(message);
    }

    public void setOnReturnCallback(Runnable callback) {
        this.onReturnCallback = callback;
    }

    @FXML
    private void handleReturnToMenu(ActionEvent event) {
        if (onReturnCallback != null) {
            onReturnCallback.run();
        }
    }

}
