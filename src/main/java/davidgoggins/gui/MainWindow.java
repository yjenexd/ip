package davidgoggins.gui;

import davidgoggins.DavidGoggins;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

/**
 * Controller for the main window: the scrolling conversation and the input box.
 *
 * <p>Holds no chatbot logic of its own. It turns a typed line into a call on
 * {@link DavidGoggins#getResponse(String)} and puts the two sides of the exchange on
 * screen as dialog boxes.
 */
public class MainWindow extends AnchorPane {

    /** How long the farewell stays on screen before the window closes. */
    private static final Duration EXIT_DELAY = Duration.seconds(1.5);

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private VBox dialogContainer;

    @FXML
    private TextField userInput;

    @FXML
    private Button sendButton;

    /** The chatbot that answers each command; set by the class that builds the window. */
    private DavidGoggins davidGoggins;

    private final Image userImage = new Image(this.getClass().getResourceAsStream("/images/DaUser.png"));
    private final Image botImage = new Image(this.getClass().getResourceAsStream("/images/DaDavidGoggins.png"));

    /** Keeps the newest message in view as the conversation grows. */
    @FXML
    public void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
    }

    /**
     * Gives the window the chatbot to talk to, and greets the user.
     *
     * <p>The greeting is shown here rather than in {@link #initialize()} because it
     * needs the chatbot, which does not exist yet when the FXML is loaded.
     *
     * @param davidGoggins the chatbot that answers the user's commands
     */
    public void setDavidGoggins(DavidGoggins davidGoggins) {
        this.davidGoggins = davidGoggins;
        addBotDialog(davidGoggins.getGreeting());

        // A returning user is shown the list restored from the save file, matching
        // what the text UI does at start-up.
        String restored = davidGoggins.getRestoredTasks();
        if (!restored.isEmpty()) {
            addBotDialog(restored);
        }
    }

    /**
     * Answers whatever the user typed and shows both sides of the exchange.
     *
     * <p>On {@code bye} the reply is shown first and the window closes a moment later,
     * so the farewell is readable instead of vanishing with the window.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText();
        if (input.isBlank()) {
            return;
        }

        String response = davidGoggins.getResponse(input);
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input, userImage),
                DialogBox.getBotDialog(response, botImage));
        userInput.clear();

        if (davidGoggins.isExitCommand(input)) {
            userInput.setDisable(true);
            sendButton.setDisable(true);
            PauseTransition pause = new PauseTransition(EXIT_DELAY);
            pause.setOnFinished(event -> Platform.exit());
            pause.play();
        }
    }

    /** Adds one message from the chatbot, used for messages the user did not prompt. */
    private void addBotDialog(String message) {
        dialogContainer.getChildren().add(DialogBox.getBotDialog(message, botImage));
    }
}
