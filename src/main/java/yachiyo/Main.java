package yachiyo;

import java.net.URL;
import java.util.Objects;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import yachiyo.ui.DialogBox;

/**
 * Displays the JavaFX user interface for Yachiyo.
 */
public class Main extends Application {
    private final Yachiyo yachiyo = new Yachiyo();

    private VBox dialogContainer;
    private TextField userInput;
    private Image userImage;
    private Image yachiyoImage;

    /**
     * Displays the chat interface.
     *
     * @param stage primary window supplied by JavaFX.
     */
    @Override
    public void start(Stage stage) {
        ScrollPane scrollPane = new ScrollPane();
        dialogContainer = new VBox();
        scrollPane.setContent(dialogContainer);

        userInput = new TextField();
        Button sendButton = new Button("Send");

        userImage = loadImage("/images/DaUser.png");
        yachiyoImage = loadImage("/images/DaDuke.png");

        AnchorPane mainLayout = new AnchorPane();
        mainLayout.getChildren().addAll(scrollPane, userInput, sendButton);

        stage.setTitle("Yachiyo");
        stage.setResizable(false);
        stage.setMinHeight(600.0);
        stage.setMinWidth(400.0);

        mainLayout.setPrefSize(400.0, 600.0);
        scrollPane.setPrefSize(385.0, 535.0);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        scrollPane.setVvalue(1.0);
        scrollPane.setFitToWidth(true);

        dialogContainer.setPrefHeight(Region.USE_COMPUTED_SIZE);

        userInput.setPrefWidth(325.0);
        sendButton.setPrefWidth(55.0);

        AnchorPane.setTopAnchor(scrollPane, 1.0);
        AnchorPane.setBottomAnchor(sendButton, 1.0);
        AnchorPane.setRightAnchor(sendButton, 1.0);
        AnchorPane.setLeftAnchor(userInput, 1.0);
        AnchorPane.setBottomAnchor(userInput, 1.0);

        sendButton.setOnMouseClicked(event -> handleUserInput());
        userInput.setOnAction(event -> handleUserInput());
        dialogContainer.heightProperty().addListener(
                observable -> scrollPane.setVvalue(1.0)
        );

        Scene scene = new Scene(mainLayout);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Adds the user's message to the dialog pane and clears the input field.
     */
    private void handleUserInput() {
        String userText = userInput.getText();
        String yachiyoText = yachiyo.getResponse(userText);
        dialogContainer.getChildren().addAll(
                new DialogBox(userText, userImage),
                new DialogBox(yachiyoText, yachiyoImage)
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
