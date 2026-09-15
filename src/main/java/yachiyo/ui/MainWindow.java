package yachiyo.ui;

import java.net.URL;
import java.util.Objects;

import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
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
    private Label taskSummary;

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
        assert scrollPane != null
                && taskSummary != null
                && dialogContainer != null
                && userInput != null
                && sendButton != null
                : "Main-window controls must be injected from FXML";

        dialogContainer.heightProperty().addListener((observable, oldHeight, newHeight) ->
                scrollPane.setVvalue(scrollPane.getVmax()));
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

        String initializationResponse = yachiyo.initialize();
        if (!initializationResponse.isEmpty()) {
            dialogContainer.getChildren().add(createYachiyoDialog(initializationResponse));
        }
        updateTaskSummary();
    }

    /**
     * Adds the user's message and Yachiyo's response, then clears the input field.
     */
    @FXML
    private void handleUserInput() {
        assert yachiyo != null : "Yachiyo must be supplied before input is handled";

        String input = userInput.getText().trim();
        if (input.isEmpty()) {
            return;
        }

        String response = yachiyo.getResponse(input);
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input, userImage),
                createYachiyoDialog(response)
        );
        updateTaskSummary();
        userInput.clear();

        if (yachiyo.isExitRequested()) {
            disableInputAndScheduleClose();
        }
    }

    /**
     * Creates a standard or categorized error dialog for a Yachiyo response.
     *
     * @param response response to display.
     * @return dialog containing the response.
     */
    private DialogBox createYachiyoDialog(String response) {
        return yachiyo.getLastErrorCategory()
                .map(category -> DialogBox.getYachiyoErrorDialog(response, yachiyoImage, category))
                .orElseGet(() -> DialogBox.getYachiyoDialog(response, yachiyoImage));
    }

    /**
     * Updates the pinned summary using the latest loaded task counts.
     */
    private void updateTaskSummary() {
        if (!yachiyo.hasLoadedTasks()) {
            taskSummary.setText("Tasks unavailable");
            return;
        }

        int taskCount = yachiyo.getTaskCount();
        int remainingCount = yachiyo.getRemainingTaskCount();
        String taskNoun = taskCount == 1 ? "task" : "tasks";
        taskSummary.setText(String.format("%d %s · %d remaining",
                taskCount, taskNoun, remainingCount));
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
