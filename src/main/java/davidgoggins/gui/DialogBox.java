package davidgoggins.gui;

import java.io.IOException;
import java.util.Collections;

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
 * One message in the conversation: a picture beside the words that were said.
 *
 * <p>Built from {@code DialogBox.fxml} through the {@code fx:root} pattern, so the
 * layout lives in the FXML while this class supplies the two pieces of content.
 */
public class DialogBox extends HBox {

    @FXML
    private Label dialog;

    @FXML
    private ImageView displayPicture;

    /**
     * Creates a dialog box showing the given text next to the given picture.
     *
     * <p>Private because the two static factory methods below say which side of the
     * conversation the box belongs to, which a bare constructor could not.
     *
     * @param text the words to show
     * @param image the picture of whoever said them
     */
    private DialogBox(String text, Image image) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(DialogBox.class.getResource("/view/DialogBox.fxml"));
            fxmlLoader.setController(this);
            fxmlLoader.setRoot(this);
            fxmlLoader.load();
        } catch (IOException e) {
            // Only thrown if the FXML is missing or malformed, which is a packaging
            // fault rather than anything the user can act on.
            e.printStackTrace();
        }

        dialog.setText(text);
        displayPicture.setImage(image);
    }

    /**
     * Flips the box so the picture is on the left and the text on the right.
     *
     * <p>Used for the chatbot's own messages, so the two speakers line up on opposite
     * sides of the window and are told apart at a glance.
     */
    private void flip() {
        ObservableList<Node> children = FXCollections.observableArrayList(this.getChildren());
        Collections.reverse(children);
        this.getChildren().setAll(children);
        this.setAlignment(Pos.TOP_LEFT);
        dialog.getStyleClass().add("reply-label");
    }

    /**
     * Returns a dialog box for something the user typed, aligned to the right.
     *
     * @param text the words the user typed
     * @param image the user's picture
     * @return the dialog box to add to the conversation
     */
    public static DialogBox getUserDialog(String text, Image image) {
        return new DialogBox(text, image);
    }

    /**
     * Returns a dialog box for the chatbot's reply, aligned to the left.
     *
     * @param text the chatbot's reply
     * @param image the chatbot's picture
     * @return the dialog box to add to the conversation
     */
    public static DialogBox getBotDialog(String text, Image image) {
        DialogBox box = new DialogBox(text, image);
        box.flip();
        return box;
    }
}
