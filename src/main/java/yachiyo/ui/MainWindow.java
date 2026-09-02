package yachiyo.ui;

import java.net.URL;
import java.util.Objects;

import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import yachiyo.Yachiyo;

/**
 * Controls the main chat window defined in FXML.
 */
public class MainWindow extends AnchorPane {
    private final Image userImage = loadImage("/images/DaUser.png");
    private final Image yachiyoImage = loadImage("/images/DaDuke.png");

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private VBox dialogContainer;

    @FXML
    private TextField userInput;

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
    }

    /**
     * Adds the user's message and Yachiyo's response, then clears the input field.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText();
        String response = yachiyo.getResponse(input);
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input, userImage),
                DialogBox.getYachiyoDialog(response, yachiyoImage)
        );
        userInput.clear();
    }

    private Image loadImage(String imagePath) {
        URL imageUrl = Objects.requireNonNull(
                getClass().getResource(imagePath),
                "Image resource not found: " + imagePath
        );
        return new Image(imageUrl.toExternalForm());
    }
}
