package yachiyo;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

/**
 * Displays the JavaFX user interface for Yachiyo.
 */
public class Main extends Application {
    /**
     * Displays a window containing a Hello World label.
     *
     * @param stage primary window supplied by JavaFX.
     */
    @Override
    public void start(Stage stage) {
        Label helloWorld = new Label("Hello World!");
        Scene scene = new Scene(helloWorld);
        stage.setScene(scene);
        stage.show();
    }
}
