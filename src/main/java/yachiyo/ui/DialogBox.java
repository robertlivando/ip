package yachiyo.ui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Node;
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

    /**
     * Creates a user dialog with its profile image on the right.
     *
     * @param message message to display.
     * @param image profile image to display.
     * @return user dialog box.
     */
    public static DialogBox getUserDialog(String message, Image image) {
        return new DialogBox(message, image);
    }

    /**
     * Creates a Yachiyo dialog with its profile image on the left.
     *
     * @param message message to display.
     * @param image profile image to display.
     * @return Yachiyo dialog box.
     */
    public static DialogBox getYachiyoDialog(String message, Image image) {
        DialogBox dialogBox = new DialogBox(message, image);
        dialogBox.flip();
        return dialogBox;
    }

    /**
     * Flips the dialog box so its profile image appears on the left.
     */
    private void flip() {
        setAlignment(Pos.TOP_LEFT);
        ObservableList<Node> children = FXCollections.observableArrayList(getChildren());
        FXCollections.reverse(children);
        getChildren().setAll(children);
    }
}
