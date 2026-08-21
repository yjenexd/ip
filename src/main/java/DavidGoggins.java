import java.util.List;
import java.util.Scanner;
/**
 * Entry point for the David Goggins chatbot.
 *
 * <p>For this increment the chatbot does not yet hold a conversation: it prints
 * its banner, greets the user, says goodbye, and exits.
 */
public class DavidGoggins {
    /** The name the chatbot introduces itself by. */
    private static final String NAME = "David Goggins";
    private static final String DIVIDER = "_".repeat(60);
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
    private static List<String> userInputList = new java.util.ArrayList<>(100);

    /** The command that ends the conversation. */
    private static final String EXIT_COMMAND = "bye";

    public static void main(String[] args) {
        greet();

        // Scanner is used to read user input from the console. It is closed automatically at the end of the try block.
        try (Scanner scanner = new Scanner(System.in)) {
            // hasNextLine() returns false at end of input, so redirected input
            // that omits "bye" exits cleanly instead of throwing.
            while (scanner.hasNextLine()) {
                String userInput = scanner.nextLine().trim();

                if (userInput.equalsIgnoreCase(EXIT_COMMAND)) {
                    break;
                }

                if (userInput.equalsIgnoreCase("list")) {
                    System.out.println(DIVIDER);
                    System.out.print(printUserInput(userInputList));
                    System.out.println(DIVIDER);
                    System.out.println();
                    continue;
                }

                userInputList.add(userInput);
                echo(userInput);
            }
        }

        farewell();
    }

    /**
     * Prints the user's command back to them, wrapped in dividers.
     *
     * @param userInput the line the user typed
     */
    private static void echo(String userInput) {
        System.out.println(DIVIDER);
        System.out.println("added: " + userInput);
        System.out.println(DIVIDER);
        System.out.println();
    }

    /** Prints the banner and the opening greeting. */
    private static void greet() {
        System.out.println(DIVIDER);
        System.out.println(BANNER);
        System.out.println("Hello! I'm " + NAME + ".");
        System.out.println("What can I do for you?");
        System.out.println(DIVIDER);
        System.out.println();
    }

    /** Prints the sign-off shown as the chatbot exits. */
    private static void farewell() {
        System.out.println(DIVIDER);
        System.out.println("Bye. Remember, stay hard!");
        System.out.println(DIVIDER);
    }

    /* Prints the list of user inputs. */
    private static String printUserInput(List<String> userInput) {
        StringBuilder sb = new StringBuilder();
        int counter = 1;
        for (String input : userInput) {
            sb.append(counter).append(". ").append(input).append("\n");
            counter++;
        }
        return sb.toString();
    }
}
