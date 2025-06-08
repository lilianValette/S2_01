package com.bomberman.controller;

import com.bomberman.model.Character;
import com.bomberman.model.UserManager;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class CharacterSelectionController {
    @FXML private StackPane rootPane;
    @FXML private ImageView backgroundImage;
    @FXML private Label titleLabel;
    @FXML private Label characterNameLabel;

    // Images des personnages
    @FXML private ImageView character1Image;
    @FXML private ImageView character2Image;
    @FXML private ImageView character3Image;
    @FXML private ImageView character4Image;
    @FXML private ImageView selectedCharacterPreview;

    // Boutons de sélection
    @FXML private Button selectCharacter1Button;
    @FXML private Button selectCharacter2Button;
    @FXML private Button selectCharacter3Button;
    @FXML private Button selectCharacter4Button;

    // Boutons d'action
    @FXML private Button confirmButton;
    @FXML private Button backButton;

    private Stage stage;
    private UserManager userManager;
    private Character selectedCharacter;

    public void setStage(Stage stage) {
        this.stage = stage;
        // Définir une taille fixe
        stage.setWidth(800);
        stage.setHeight(600);
        stage.setMinWidth(800);
        stage.setMinHeight(600);
        stage.setMaxWidth(800);
        stage.setMaxHeight(600);
        stage.setResizable(false);
        stage.centerOnScreen();
        stage.setTitle("Sélection de personnage - Bomberman");

        // Charger le CSS immédiatement
        loadStylesheet();
    }

    @FXML
    public void initialize() {
        System.out.println("=== Initialisation CharacterSelectionController ===");

        userManager = UserManager.getInstance();

        // Charger l'image de fond en premier
        loadBackgroundImage();

        // Initialiser avec le personnage actuel de l'utilisateur
        selectedCharacter = userManager.getCurrentUserCharacter();
        if (selectedCharacter == null) {
            selectedCharacter = Character.BOMBERMAN_1; // Par défaut
        }
        System.out.println("Personnage initial : " + selectedCharacter.getDisplayName());

        // Charger les images des personnages
        loadCharacterImages();

        // Configurer les boutons
        setupButtons();

        // Mettre à jour l'affichage
        updateSelectedCharacterDisplay();

        // Délai pour s'assurer que tout est chargé avant d'appliquer les styles
        Platform.runLater(() -> {
            setupButtonStyles();
            System.out.println("Styles appliqués");
        });
    }

    private void loadStylesheet() {
        try {
            if (stage != null && stage.getScene() != null) {
                java.net.URL cssUrl = getClass().getResource("/css/character-selection-style.css");
                if (cssUrl != null) {
                    String cssPath = cssUrl.toExternalForm();
                    stage.getScene().getStylesheets().clear();
                    stage.getScene().getStylesheets().add(cssPath);
                    System.out.println("CSS chargé : " + cssPath);
                } else {
                    System.err.println("CSS non trouvé : /css/character-selection-style.css");
                }
            }
        } catch (Exception e) {
            System.err.println("Erreur chargement CSS : " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void setupButtonStyles() {
        try {
            // Appliquer les styles aux boutons de personnage
            if (selectCharacter1Button != null) setupButtonStyle(selectCharacter1Button, "character-button");
            if (selectCharacter2Button != null) setupButtonStyle(selectCharacter2Button, "character-button");
            if (selectCharacter3Button != null) setupButtonStyle(selectCharacter3Button, "character-button");
            if (selectCharacter4Button != null) setupButtonStyle(selectCharacter4Button, "character-button");

            // Appliquer les styles aux boutons d'action
            if (confirmButton != null) setupButtonStyle(confirmButton, "game-button-primary");
            if (backButton != null) setupButtonStyle(backButton, "game-button-secondary");

            // Mettre en évidence le personnage sélectionné
            updateButtonStyles();

            System.out.println("Tous les styles de boutons appliqués");
        } catch (Exception e) {
            System.err.println("Erreur lors de l'application des styles : " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void setupButtonStyle(Button button, String styleClass) {
        if (button != null) {
            Platform.runLater(() -> {
                button.getStyleClass().clear();
                button.getStyleClass().add(styleClass);
                button.applyCss();
                System.out.println("Style appliqué à " + button.getId() + " : " + styleClass);
            });
        }
    }

    private void loadBackgroundImage() {
        try {
            java.net.URL url = getClass().getResource("/images/menu/Bomber_fond.jpg");
            if (url != null) {
                Image image = new Image(url.toExternalForm());
                if (backgroundImage != null) {
                    backgroundImage.setImage(image);
                    backgroundImage.setFitWidth(800);
                    backgroundImage.setFitHeight(600);
                    backgroundImage.setPreserveRatio(false);
                    System.out.println("Image de fond chargée");
                }
            } else {
                System.err.println("Image de fond non trouvée : /images/menu/Bomber_fond.jpg");
            }
        } catch (Exception e) {
            System.err.println("Erreur chargement image de fond : " + e.getMessage());
        }
    }

    private void loadCharacterImages() {
        System.out.println("Chargement des images de personnages...");
        loadCharacterImage(character1Image, Character.BOMBERMAN_1, "Character 1");
        loadCharacterImage(character2Image, Character.BOMBERMAN_2, "Character 2");
        loadCharacterImage(character3Image, Character.BOMBERMAN_3, "Character 3");
        loadCharacterImage(character4Image, Character.BOMBERMAN_4, "Character 4");
    }

    private void loadCharacterImage(ImageView imageView, Character character, String debugName) {
        if (imageView == null) {
            System.err.println("ImageView null pour " + debugName);
            return;
        }

        try {
            String imagePath = character.getFrontImagePath();
            System.out.println("Tentative de chargement : " + imagePath + " pour " + debugName);

            java.net.URL url = getClass().getResource(imagePath);
            if (url != null) {
                Image image = new Image(url.toExternalForm());
                imageView.setImage(image);
                imageView.setFitWidth(80);
                imageView.setFitHeight(80);
                imageView.setPreserveRatio(true);
                System.out.println("✓ Image chargée pour " + debugName + " : " + character.getDisplayName());
            } else {
                System.err.println("✗ Image non trouvée : " + imagePath + " pour " + debugName);
                // Essayer avec un chemin alternatif ou une image par défaut
                loadDefaultCharacterImage(imageView, debugName);
            }
        } catch (Exception e) {
            System.err.println("Erreur chargement image " + debugName + " : " + e.getMessage());
            loadDefaultCharacterImage(imageView, debugName);
        }
    }

    private void loadDefaultCharacterImage(ImageView imageView, String debugName) {
        try {
            // Essayer avec une image par défaut simple
            java.net.URL url = getClass().getResource("/images/Player/default.png");
            if (url != null) {
                Image image = new Image(url.toExternalForm());
                imageView.setImage(image);
                System.out.println("Image par défaut chargée pour " + debugName);
            } else {
                System.err.println("Aucune image par défaut disponible pour " + debugName);
            }
        } catch (Exception e) {
            System.err.println("Erreur chargement image par défaut pour " + debugName + " : " + e.getMessage());
        }
    }

    private void setupButtons() {
        System.out.println("Configuration des boutons...");

        if (selectCharacter1Button != null) {
            selectCharacter1Button.setOnAction(e -> selectCharacter(Character.BOMBERMAN_1));
            System.out.println("✓ Bouton 1 configuré");
        }
        if (selectCharacter2Button != null) {
            selectCharacter2Button.setOnAction(e -> selectCharacter(Character.BOMBERMAN_2));
            System.out.println("✓ Bouton 2 configuré");
        }
        if (selectCharacter3Button != null) {
            selectCharacter3Button.setOnAction(e -> selectCharacter(Character.BOMBERMAN_3));
            System.out.println("✓ Bouton 3 configuré");
        }
        if (selectCharacter4Button != null) {
            selectCharacter4Button.setOnAction(e -> selectCharacter(Character.BOMBERMAN_4));
            System.out.println("✓ Bouton 4 configuré");
        }

        if (confirmButton != null) {
            confirmButton.setOnAction(e -> confirmSelection());
            System.out.println("✓ Bouton confirmer configuré");
        }
        if (backButton != null) {
            backButton.setOnAction(e -> returnToProfile());
            System.out.println("✓ Bouton retour configuré");
        }
    }

    private void selectCharacter(Character character) {
        System.out.println("=== Sélection personnage : " + character.getDisplayName() + " ===");
        selectedCharacter = character;
        updateSelectedCharacterDisplay();
        updateButtonStyles();
    }

    private void updateSelectedCharacterDisplay() {
        if (selectedCharacter != null) {
            System.out.println("Mise à jour affichage pour : " + selectedCharacter.getDisplayName());

            if (characterNameLabel != null) {
                characterNameLabel.setText(selectedCharacter.getDisplayName());
            }

            // Mettre à jour l'aperçu du personnage sélectionné
            if (selectedCharacterPreview != null) {
                try {
                    java.net.URL url = getClass().getResource(selectedCharacter.getFrontImagePath());
                    if (url != null) {
                        Image image = new Image(url.toExternalForm());
                        selectedCharacterPreview.setImage(image);
                        selectedCharacterPreview.setFitWidth(120);
                        selectedCharacterPreview.setFitHeight(120);
                        selectedCharacterPreview.setPreserveRatio(true);
                        System.out.println("✓ Aperçu mis à jour");
                    }
                } catch (Exception e) {
                    System.err.println("Erreur mise à jour aperçu : " + e.getMessage());
                }
            }
        }
    }

    private void updateButtonStyles() {
        Platform.runLater(() -> {
            try {
                // Réinitialiser tous les boutons avec le style par défaut
                if (selectCharacter1Button != null) setupButtonStyle(selectCharacter1Button, "character-button");
                if (selectCharacter2Button != null) setupButtonStyle(selectCharacter2Button, "character-button");
                if (selectCharacter3Button != null) setupButtonStyle(selectCharacter3Button, "character-button");
                if (selectCharacter4Button != null) setupButtonStyle(selectCharacter4Button, "character-button");

                // Attendre un peu puis mettre en évidence le bouton sélectionné
                Platform.runLater(() -> {
                    Button selectedButton = null;
                    switch (selectedCharacter) {
                        case BOMBERMAN_1:
                            selectedButton = selectCharacter1Button;
                            break;
                        case BOMBERMAN_2:
                            selectedButton = selectCharacter2Button;
                            break;
                        case BOMBERMAN_3:
                            selectedButton = selectCharacter3Button;
                            break;
                        case BOMBERMAN_4:
                            selectedButton = selectCharacter4Button;
                            break;
                    }

                    if (selectedButton != null) {
                        selectedButton.getStyleClass().clear();
                        selectedButton.getStyleClass().add("character-button-selected");
                        selectedButton.applyCss();
                        System.out.println("✓ Bouton sélectionné mis en évidence : " + selectedButton.getId());
                    }
                });
            } catch (Exception e) {
                System.err.println("Erreur mise à jour styles boutons : " + e.getMessage());
            }
        });
    }

    private void confirmSelection() {
        if (selectedCharacter != null && userManager.getCurrentUser() != null) {
            System.out.println("=== Confirmation sélection : " + selectedCharacter.getDisplayName() + " ===");
            userManager.updateCurrentUserCharacter(selectedCharacter);
            returnToProfile();
        } else {
            System.err.println("Erreur : personnage=" + selectedCharacter + ", utilisateur=" + userManager.getCurrentUser());
        }
    }

    private void returnToProfile() {
        try {
            System.out.println("=== Retour au profil ===");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/bomberman/view/account.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root);

            // Appliquer le CSS du compte
            try {
                java.net.URL cssUrl = getClass().getResource("/css/account-style.css");
                if (cssUrl != null) {
                    String cssPath = cssUrl.toExternalForm();
                    scene.getStylesheets().add(cssPath);
                    System.out.println("CSS du compte appliqué : " + cssPath);
                }
            } catch (Exception e) {
                System.err.println("Erreur chargement CSS compte : " + e.getMessage());
            }

            AccountController accountController = loader.getController();
            stage.setScene(scene);
            accountController.setStage(stage);

        } catch (Exception ex) {
            System.err.println("Erreur retour au profil : " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}