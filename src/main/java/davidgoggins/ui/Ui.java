package davidgoggins.ui;

import java.util.Scanner;

/**
 * Deals with everything the user sees and types.
 *
 * <p>All console reading and writing is gathered here, so the divider width, the banner
 * and the "OOPS!" error prefix are written once and the look of the chatbot changes in
 * this class alone. Input belongs here too: a command and its reply are one exchange.
 */
public class Ui {
    /** The name the chatbot introduces itself by. */
    private static final String NAME = "David Goggins";

    /** The horizontal rule printed above and below every reply block. */
    private static final String DIVIDER = "_".repeat(60);

    /** The prefix put in front of every error message shown to the user. */
    private static final String ERROR_PREFIX = " OOPS! ";

    /** The prefix put in front of every housekeeping warning shown to the user. */
    private static final String WARNING_PREFIX = " Warning: ";

    private static final String BANNER = """

            +----------------------------------------------------------+
            |      ____      _    __     __ ___  ____                  |
            |     |  _ \\    / \\   \\ \\   / /|_ _||  _ \\                 |
            |     | | | |  / _ \\   \\ \\ / /  | | | | | |                |
            |     | |_| | / ___ \\   \\ V /   | | | |_| |                |
            |     |____/ /_/   \\_\\   \\_/   |___||____/                 |
            |       ____   ___    ____   ____  ___  _   _  ____        |
            |      / ___| / _ \\  / ___| / ___||_ _|| \\ | |/ ___|       |
            |     | |  _ | | | || |  _ | |  _  | | |  \\| |\\___ \\       |
            |     | |_| || |_| || |_| || |_| | | | | |\\  | ___) |      |
            |      \\____| \\___/  \\____| \\____||___||_| \\_||____/       |
            |                                                          |
            |       __                                       __        |
            |      /  \\                                     /  \\       |
            |     | ## |===================================| ## |      |
            |     | ## |===================================| ## |      |
            |      \\__/                                     \\__/       |
            |                                                          |
            |       "WHO'S GONNA CARRY THE BOATS AND THE LOGS?!"       |
            |                 THEY DON'T KNOW ME, SON!                 |
            |                                                          |
            |                     >> STAY HARD. <<                     |
            +----------------------------------------------------------+
            """.stripTrailing();

    /** Reads the user's commands from the console, one line at a time. */
    private final Scanner scanner = new Scanner(System.in);

    /**
     * Returns true if there is another command waiting to be read.
     *
     * <p>This is false at the end of input, so a run whose input is piped in from a
     * file and does not end with {@code bye} exits cleanly instead of throwing.
     */
    public boolean hasNextCommand() {
        return scanner.hasNextLine();
    }

    /**
     * Reads the next command line, with surrounding spaces removed.
     *
     * <p>Trimming here rather than in the caller means every command is given the same
     * treatment, so a stray leading space can never turn a valid command into an
     * unknown one.
     */
    public String readCommand() {
        return scanner.nextLine().trim();
    }

    /**
     * Prints one reply block: the given lines between two dividers.
     *
     * <p>Every response has this same shape, so keeping it in one place means the
     * layout is defined once rather than repeated in each command.
     *
     * @param lines the lines to print, already spaced as they should appear
     */
    public void show(String... lines) {
        System.out.println(DIVIDER);
        for (String line : lines) {
            System.out.println(line);
        }
        System.out.println(DIVIDER);
        System.out.println();
    }

    /**
     * Reports a problem to the user in the standard error block.
     *
     * <p>Kept separate from {@link #show} so that the error wording is decided here
     * once, instead of every caller having to remember the prefix.
     *
     * @param message the explanation to show, without the prefix
     */
    public void showError(String message) {
        show(ERROR_PREFIX + message);
    }

    /**
     * Reports a problem the program worked around, such as a save that did not happen.
     *
     * <p>Printed as a bare line rather than inside a divider block: it appears before
     * the greeting at start-up, where a reply block would look like an answer to a
     * command the user never typed.
     *
     * @param message the explanation to show, without the "Warning:" label
     */
    public void showWarning(String message) {
        System.out.println(WARNING_PREFIX + message);
    }

    /** Prints the banner and the opening greeting. */
    public void showWelcome() {
        System.out.println(DIVIDER);
        System.out.println(BANNER);
        System.out.println("Hello! I'm " + NAME + ".");
        System.out.println("What can I do for you?");
        System.out.println(DIVIDER);
        System.out.println();
    }

    /** Prints the sign-off shown as the chatbot exits. */
    public void showFarewell() {
        System.out.println(DIVIDER);
        System.out.println("Bye. Remember, stay hard!");
        System.out.println(DIVIDER);
    }

    /** Stops reading input. Called once the conversation has ended. */
    public void close() {
        scanner.close();
    }
}
