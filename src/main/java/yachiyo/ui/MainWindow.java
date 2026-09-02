package yachiyo.ui;

import java.net.URL;
import java.util.Objects;

import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import yachiyo.Yachiyo;

/**
 * Controls the main chat window defined in FXML.
 */
public class MainWindow extends AnchorPane {
    private static final Duration EXIT_DELAY = Duration.millis(2000);

    private final Image userImage = loadImage("/images/user-profile.png");
    private final Image yachiyoImage = loadImage("/images/yachiyo-profile.png");

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private VBox dialogContainer;

    @FXML
    private TextField userInput;

    @FXML
    private Button sendButton;

    private Yachiyo yachiyo;

    /**
     * Configures automatic scrolling after the FXML fields are injected.
     */
    @FXML
    private void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
    }

    /**
     * Supplies the chatbot that generates responses to user messages.
     *
     * @param yachiyo chatbot used by this window.
     */
    public void setYachiyo(Yachiyo yachiyo) {
        this.yachiyo = yachiyo;
        dialogContainer.getChildren().add(
                DialogBox.getYachiyoDialog(yachiyo.getGreeting(), yachiyoImage)
        );
    }

    /**
     * Adds the user's message and Yachiyo's response, then clears the input field.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText().trim();
        if (input.isEmpty()) {
            return;
        }

        String response = yachiyo.getResponse(input);
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input, userImage),
                DialogBox.getYachiyoDialog(response, yachiyoImage)
        );
        userInput.clear();

        if (yachiyo.isExitRequested()) {
            disableInputAndScheduleClose();
        }
    }

    /**
     * Prevents further input and closes the window after the farewell can be read.
     */
    private void disableInputAndScheduleClose() {
        userInput.setDisable(true);
        sendButton.setDisable(true);

        PauseTransition closeDelay = new PauseTransition(EXIT_DELAY);
        closeDelay.setOnFinished(event -> ((Stage) userInput.getScene().getWindow()).close());
        closeDelay.play();
    }

    private Image loadImage(String imagePath) {
        URL imageUrl = Objects.requireNonNull(
                getClass().getResource(imagePath),
                "Image resource not found: " + imagePath
        );
        return new Image(imageUrl.toExternalForm());
    }
}
