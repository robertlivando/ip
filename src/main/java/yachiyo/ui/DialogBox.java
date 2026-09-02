package yachiyo.ui;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

/**
 * Displays a chat message beside its sender's profile image.
 */
public class DialogBox extends HBox {
    /**
     * Creates a dialog box containing the supplied message and profile image.
     *
     * @param message message to display.
     * @param image profile image to display.
     */
    public DialogBox(String message, Image image) {
        Label text = new Label(message);
        ImageView displayPicture = new ImageView(image);

        text.setWrapText(true);
        displayPicture.setFitWidth(100.0);
        displayPicture.setFitHeight(100.0);
        setAlignment(Pos.TOP_RIGHT);

        getChildren().addAll(text, displayPicture);
    }
}
