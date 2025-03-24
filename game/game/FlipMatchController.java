package game;

import javafx.animation.ScaleTransition;
import javafx.animation.SequentialTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FlipMatchController {

    @FXML
    private Button card1, card2, card3, card4, card5, card6, card7, card8, card9, card10, card11, card12;

    private List<String> images;
    private Button selectedButton = null;
    private String selectedImage = null;
    private boolean isProcessing = false; // Prevents rapid clicks
    private Set<Button> matchedButtons = new HashSet<>(); // Tracks matched buttons
    private Set<Button> flippedButtons = new HashSet<>(); // Tracks currently flip buttons
    
    //Making the back of the cards.
    //Also using final to make sure that the back of the cards cannot be altered.
    private final Background cardBackBackground = new Background(new BackgroundFill(Color.ORANGE, CornerRadii.EMPTY, Insets.EMPTY));
    private final Border cardBorder = new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(4))); // Bold border
    private final Insets cardPadding = new Insets(10); // Add 10 pixels of consistent padding for all cards
    //back of cards end here
    
    public void initialize() {
        // Initialize the images list with pairs of images, so the same image is being added twice here.
    	//Do not delete the second images.add or game won't work.
        images = new ArrayList<>();
        for (int i = 1; i <= 6; i++) { //if there is less than 6 images in the array, then keep adding images, or else stop at 6.
            images.add("/game/image" + i + ".png");
            images.add("/game/image" + i + ".png");
        }

        // Shuffle the images list
        Collections.shuffle(images);

        // Reset the cards to their initial state
        resetCards();
    }

    @FXML
    private void handleCardClick(javafx.event.ActionEvent event) {
        if (isProcessing) {
            return; // Ignore click if a match check is in progress
        }

        Button clickedButton = (Button) event.getSource();
        if (matchedButtons.contains(clickedButton) || flippedButtons.contains(clickedButton)) {
            return; // Ignore click if the card is already matched or currently flip
        }

        int index = getButtonIndex(clickedButton);
        String image = images.get(index);

        if (selectedButton == null) {
            // First card selected
            selectedButton = clickedButton;
            selectedImage = image;
            isProcessing = true;
            flippedButtons.add(clickedButton);
            flipCard(clickedButton, () -> {
                showImage(clickedButton, image);
                isProcessing = false;
            });
        } else {
            // Second card selected
            isProcessing = true;
            flippedButtons.add(clickedButton);
            flipCard(clickedButton, () -> {
                showImage(clickedButton, image);
                if (selectedButton != clickedButton) {
                    if (selectedImage.equals(image)) {
                        // Match found
                        matchedButtons.add(selectedButton);
                        matchedButtons.add(clickedButton);
                        resetSelectionState();
                        isProcessing = false; // Unlock
                    } else {
                        // No match found
                        new Thread(() -> {
                            try {
                                Thread.sleep(1000); // Wait before hiding images
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            Platform.runLater(() -> {
                                flipCard(selectedButton, () -> hideImage(selectedButton));
                                flipCard(clickedButton, () -> {
                                    hideImage(clickedButton);
                                    resetSelectionState();
                                    isProcessing = false; // Unlock
                                });
                            });
                        }).start();
                    }
                } else {
                    isProcessing = false;
                }
            });
        }
    }

    private int getButtonIndex(Button button) {
        // Get the index of the button based on its reference
        if (button == card1) return 0;
        if (button == card2) return 1;
        if (button == card3) return 2;
        if (button == card4) return 3;
        if (button == card5) return 4;
        if (button == card6) return 5;
        if (button == card7) return 6;
        if (button == card8) return 7;
        if (button == card9) return 8;
        if (button == card10) return 9;
        if (button == card11) return 10;
        if (button == card12) return 11;
        return -1;
    }

    private void showImage(Button button, String image) {
        // Show the image on the button
        ImageView imageView = new ImageView(new Image(image));
        imageView.setFitWidth(button.getPrefWidth() - 20); // Adjust image size based on button size
        imageView.setFitHeight(button.getPrefHeight() - 20); // Adjust image size based on button size
        imageView.setPreserveRatio(true);
        button.setGraphic(imageView);
        button.setBackground(Background.EMPTY); // Remove background when showing image
        button.setBorder(cardBorder); // Set border when showing image
    }

    private void hideImage(Button button) {
        // Hide the image on the button and flip to the back of the card
        if (button != null) {
            button.setGraphic(null);
            button.setBackground(cardBackBackground); // Set orange background when hiding image
            button.setBorder(cardBorder); // Set border when hiding image
        }
        flippedButtons.remove(button);
    }

    private void resetCards() {
        // Reset all cards to their initial state
        for (Button button : new Button[]{card1, card2, card3, card4, card5, card6, card7, card8, card9, card10, card11, card12}) {
            button.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
            button.setMinSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
            button.setPadding(cardPadding); // Set consistent padding
            hideImage(button);
        }
        matchedButtons.clear(); // Clear matched buttons
        flippedButtons.clear(); // Clear flip buttons
    }

    private void resetSelectionState() {
        // Reset the selection state
        selectedButton = null;
        selectedImage = null;
    }
    
    // Set the minimum width and height for the stage
    public void setStageAndSetupListeners(Stage stage) {
        stage.setMinWidth(700); // Set minimum width to 700px
        stage.setMinHeight(800); // Set minimum height to 800px

        // Adjust card sizes when the stage size changes
        stage.widthProperty().addListener((obs, oldVal, newVal) -> adjustCardSizes(stage));
        stage.heightProperty().addListener((obs, oldVal, newVal) -> adjustCardSizes(stage));
    }

    private void adjustCardSizes(Stage stage) {
        // Calculate new card sizes based on the stage size
        double stageWidth = stage.getWidth();
        double stageHeight = stage.getHeight();

        double newCardWidth = stageWidth / 5; // Calculate new width based on stage size
        double newCardHeight = stageHeight / 4; // Calculate new height based on stage size

        setCardSize(newCardWidth, newCardHeight);
    }

    private void setCardSize(double width, double height) {
        // Set the size of each card
        for (Button button : new Button[]{card1, card2, card3, card4, card5, card6, card7, card8, card9, card10, card11, card12}) {
            button.setPrefSize(width, height);
        }
        // Update the image sizes within the buttons
        updateImageSizes();
    }

    private void updateImageSizes() {
        // Adjust the image sizes within each button
        for (Button button : new Button[]{card1, card2, card3, card4, card5, card6, card7, card8, card9, card10, card11, card12}) {
            if (button.getGraphic() instanceof ImageView) {
                ImageView imageView = (ImageView) button.getGraphic();
                imageView.setFitWidth(button.getPrefWidth() - 20); // Adjust image size based on button size
                imageView.setFitHeight(button.getPrefHeight() - 20); // Adjust image size based on button size
            }
        }
    }

    private void flipCard(Button button, Runnable onFinished) {
        // Create the flip animation
        ScaleTransition scale1 = new ScaleTransition(Duration.millis(150), button);
        scale1.setFromX(1);
        scale1.setToX(0);
        scale1.setOnFinished(e -> onFinished.run());

        ScaleTransition scale2 = new ScaleTransition(Duration.millis(150), button);
        scale2.setFromX(0);
        scale2.setToX(1);

        SequentialTransition seqTransition = new SequentialTransition(scale1, scale2);
        seqTransition.play();
    }
}
