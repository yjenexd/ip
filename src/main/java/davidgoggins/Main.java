package davidgoggins;

import java.io.IOException;

import davidgoggins.gui.MainWindow;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * The JavaFX application that shows the chatbot in a window.
 *
 * <p>Builds the window from {@code MainWindow.fxml} and hands the controller the
 * chatbot to talk to, so that the layout stays in the FXML and the wiring stays here.
 */
public class Main extends Application {

    /** The chatbot the window sends the user's commands to. */
    private final DavidGoggins davidGoggins = new DavidGoggins();

    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
            AnchorPane root = fxmlLoader.load();
            Scene scene = new Scene(root);

            stage.setScene(scene);
            stage.setTitle("David Goggins");
            stage.setMinHeight(400.0);
            stage.setMinWidth(400.0);

            fxmlLoader.<MainWindow>getController().setDavidGoggins(davidGoggins);
            stage.show();
        } catch (IOException e) {
            // Only thrown if the FXML is missing or malformed, which is a packaging
            // fault rather than anything the user can act on.
            e.printStackTrace();
        }
    }
}
