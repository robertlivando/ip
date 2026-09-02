package yachiyo.ui;

import java.io.IOException;
import java.util.Collections;
import java.util.Objects;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
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
    @FXML
    private Label dialog;

    @FXML
    private ImageView displayPicture;

    private DialogBox(String message, Image image) {
        FXMLLoader fxmlLoader = new FXMLLoader(Objects.requireNonNull(
                DialogBox.class.getResource("/view/DialogBox.fxml"),
                "Dialog box FXML resource not found."
        ));
        fxmlLoader.setController(this);
        fxmlLoader.setRoot(this);

        try {
            fxmlLoader.load();
        } catch (IOException e) {
            throw new IllegalStateException("Unable to load the dialog box FXML.", e);
        }

        dialog.setText(message);
        displayPicture.setImage(image);
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
        Collections.reverse(children);
        getChildren().setAll(children);
    }
}
