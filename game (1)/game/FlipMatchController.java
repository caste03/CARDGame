package game;

import javafx.animation.ScaleTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
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
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random; // Importing Random class
import java.util.Set;

public class FlipMatchController {

    @FXML
    private Button card1, card2, card3, card4, card5, card6, card7, card8, card9, card10, card11, card12;
    @FXML
    private Label triesLabel;
    @FXML
    private Text title;
    @FXML
    private Label additionalTriesLabel;

    private List<String> images; // List of images for the cards
    private Button selectedButton = null; // The first selected button
    private String selectedImage = null; // The image of the first selected button
    private boolean isProcessing = false; // To prevent multiple simultaneous clicks
    private Set<Button> matchedButtons = new HashSet<>(); // Set of matched buttons
    private Set<Button> flippedButtons = new HashSet<>(); // Set of currently flipped buttons
    private int additionalTries = 0; //new codes today: Counter for additional tries

    // Background for the back of the card
    private final Background cardBackBackground = new Background(new BackgroundFill(Color.ORANGE, CornerRadii.EMPTY, Insets.EMPTY));
    // Border for the cards
    private final Border cardBorder = new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(4)));
    private final Insets cardPadding = new Insets(10); // Padding for the cards

    private Set<Button> highlightedPair = new HashSet<>(); // Set of buttons with the highlighted pair

    public void initialize() {
        images = new ArrayList<>();
        for (int i = 1; i <= 6; i++) {
            images.add(getClass().getResource("/game/image" + i + ".png").toExternalForm());
            images.add(getClass().getResource("/game/image" + i + ".png").toExternalForm());
        }

        Collections.shuffle(images);
        resetCards();
        highlightRandomPair(); //new codes today
    }

    //new codes today
    private void highlightRandomPair() {
        // Find pairs of images
        List<int[]> pairs = new ArrayList<>();
        for (int i = 0; i < images.size(); i++) {
            for (int j = i + 1; j < images.size(); j++) {
                if (images.get(i).equals(images.get(j))) {
                    pairs.add(new int[]{i, j});
                }
            }
        }

        // Select a random pair to highlight
        Random random = new Random(); //new codes today: Create a new random number generator
        int[] randomPair = pairs.get(random.nextInt(pairs.size())); //new codes today: Select a random pair from the list

        Button firstButton = getButtonByIndex(randomPair[0]);
        Button secondButton = getButtonByIndex(randomPair[1]);

        if (firstButton != null && secondButton != null) {
            highlightedPair.add(firstButton);
            highlightedPair.add(secondButton);
        }
    }

    //new codes today
    private void animateBorder(Button button) {
        // Create a timeline to animate the border
        Timeline timeline = new Timeline(
            new KeyFrame(Duration.ZERO, new KeyValue(button.borderProperty(), createBorder(Color.YELLOW, 4))),
            new KeyFrame(Duration.seconds(0.5), new KeyValue(button.borderProperty(), createBorder(Color.RED, 6))),
            new KeyFrame(Duration.seconds(1), new KeyValue(button.borderProperty(), createBorder(Color.BLUE, 8))),
            new KeyFrame(Duration.seconds(1.5), new KeyValue(button.borderProperty(), createBorder(Color.YELLOW, 10)))
        );
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.setAutoReverse(true);
        timeline.play();
        button.setUserData(timeline); // Store the timeline in the button's user data for later access
    }

    //new codes today
    private Border createBorder(Color color, double width) {
        // Helper method to create a border with specified color and width
        return new Border(new BorderStroke(color, BorderStrokeStyle.DOTTED, CornerRadii.EMPTY, new BorderWidths(width)));
    }

    //new codes today
    private void stopBorderAnimation(Button button) {
        // Stop the border animation
        if (button.getUserData() instanceof Timeline) {
            Timeline timeline = (Timeline) button.getUserData();
            timeline.stop();
            button.setUserData(null); // Clear the user data
        }
    }

    private Button getButtonByIndex(int index) {
        switch (index) {
            case 0:
                return card1;
            case 1:
                return card2;
            case 2:
                return card3;
            case 3:
                return card4;
            case 4:
                return card5;
            case 5:
                return card6;
            case 6:
                return card7;
            case 7:
                return card8;
            case 8:
                return card9;
            case 9:
                return card10;
            case 10:
                return card11;
            case 11:
                return card12;
            default:
                return null;
        }
    }

    @FXML
    private void handleCardClick(javafx.event.ActionEvent event) {
        if (isProcessing) {
            return;
        }

        Button clickedButton = (Button) event.getSource();
        if (matchedButtons.contains(clickedButton) || flippedButtons.contains(clickedButton)) {
            return;
        }

        int index = getButtonIndex(clickedButton);
        String image = images.get(index);

        if (selectedButton == null) {
            selectedButton = clickedButton;
            selectedImage = image;
            isProcessing = true;
            flippedButtons.add(clickedButton);
            flipCard(clickedButton, () -> {
                showImage(clickedButton, image);
                isProcessing = false;
            });
        } else {
            isProcessing = true;
            flippedButtons.add(clickedButton);
            flipCard(clickedButton, () -> {
                showImage(clickedButton, image);
                if (selectedButton != clickedButton) {
                    if (selectedImage.equals(image)) {
                        matchedButtons.add(selectedButton);
                        matchedButtons.add(clickedButton);
                        if (highlightedPair.contains(selectedButton) && highlightedPair.contains(clickedButton)) {
                            additionalTries++; //new codes today: Increment additional tries if the highlighted pair is matched
                            triesLabel.setText(String.valueOf(additionalTries));
                        }
                        resetSelectionState();
                        isProcessing = false;
                    } else {
                        new Thread(() -> {
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            Platform.runLater(() -> {
                                flipCard(selectedButton, () -> hideImage(selectedButton));
                                flipCard(clickedButton, () -> {
                                    hideImage(clickedButton);
                                    resetSelectionState();
                                    isProcessing = false;
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
        ImageView imageView = new ImageView(new Image(image));
        imageView.setFitWidth(button.getPrefWidth() - 20);
        imageView.setFitHeight(button.getPrefHeight() - 20);
        imageView.setPreserveRatio(true);
        button.setGraphic(imageView);
        button.setBackground(Background.EMPTY);
        if (highlightedPair.contains(button)) {
            animateBorder(button); //new codes today
        } else {
            button.setBorder(cardBorder);
        }
    }

    private void hideImage(Button button) {
        if (button != null) {
            stopBorderAnimation(button); //new codes today: Stop any border animation
            button.setGraphic(null);
            button.setBackground(cardBackBackground);
            button.setBorder(cardBorder); // Ensure black solid border when hiding image
        }
        flippedButtons.remove(button);
    }

    private void resetCards() {
        for (Button button : new Button[]{card1, card2, card3, card4, card5, card6, card7, card8, card9, card10, card11, card12}) {
            button.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
            button.setMinSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
            button.setPadding(cardPadding);
            hideImage(button);
        }
        matchedButtons.clear();
        flippedButtons.clear();
        additionalTries = 0; //new codes today: Reset additional tries counter
        triesLabel.setText(String.valueOf(additionalTries));
    }

    private void resetSelectionState() {
        selectedButton = null;
        selectedImage = null;
    }

    public void setStageAndSetupListeners(Stage stage) {
        stage.setMinWidth(700);
        stage.setMinHeight(800);

        stage.widthProperty().addListener((obs, oldVal, newVal) -> adjustCardSizes(stage));
        stage.heightProperty().addListener((obs, oldVal, newVal) -> adjustCardSizes(stage));

        stage.widthProperty().addListener((obs, oldVal, newVal) -> adjustFontSize(stage));
        stage.heightProperty().addListener((obs, oldVal, newVal) -> adjustFontSize(stage));
    }

    private void adjustCardSizes(Stage stage) {
        double stageWidth = stage.getWidth();
        double stageHeight = stage.getHeight();

        double newCardWidth = stageWidth / 5;
        double newCardHeight = stageHeight / 4;

        setCardSize(newCardWidth, newCardHeight);
    }

    private void setCardSize(double width, double height) {
        for (Button button : new Button[]{card1, card2, card3, card4, card5, card6, card7, card8, card9, card10, card11, card12}) {
            button.setPrefSize(width, height);
        }
        updateImageSizes();
    }

    private void updateImageSizes() {
        for (Button button : new Button[]{card1, card2, card3, card4, card5, card6, card7, card8, card9, card10, card11, card12}) {
            if (button.getGraphic() instanceof ImageView) {
                ImageView imageView = (ImageView) button.getGraphic();
                imageView.setFitWidth(button.getPrefWidth() - 20);
                imageView.setFitHeight(button.getPrefHeight() - 20);
            }
        }
    }

    private void adjustFontSize(Stage stage) {
        double newFontSize = stage.getWidth() / 50;
        title.setFont(new Font("Arial Black", newFontSize));
        triesLabel.setFont(new Font("System Bold", newFontSize));
        additionalTriesLabel.setFont(new Font("System Bold", newFontSize));
    }

    private void flipCard(Button button, Runnable onFinished) {
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
