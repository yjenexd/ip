import java.util.Scanner;

/**
 * Entry point for the David Goggins chatbot.
 *
 * <p>Reads commands from the user until they type {@code bye}. Supported commands
 * are {@code todo}, {@code deadline}, {@code event}, {@code list}, {@code mark <n>}
 * and {@code unmark <n>}.
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

    /** The tasks the user has added so far. */
    private static final TaskList tasks = new TaskList();

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

                handleCommand(userInput);
            }
        }

        farewell();
    }

    /**
     * Works out which command the user typed and carries it out.
     *
     * <p>The input is split into the command word and the rest of the line, so that
     * {@code mark 2} gives {@code "mark"} and {@code "2"}, while a task description
     * such as {@code read book} stays intact as a single argument.
     *
     * @param userInput the line the user typed, already trimmed
     */
    private static void handleCommand(String userInput) {
        // Limit of 2 stops the split after the first space, keeping the rest whole.
        String[] parts = userInput.split("\\s+", 2); //split the input into at most two parts
        String command = parts[0].toLowerCase();
        String argument = parts.length > 1 ? parts[1] : ""; 

        switch (command) {
        case "list" -> showTasks();
        case "mark" -> setDone(argument, true); // set the argument(number) task as done
        case "unmark" -> setDone(argument, false); // set the argument(number) task as not done yet
        case "todo" -> addTask(new Todo(argument));
        case "deadline" -> addDeadline(argument);
        case "event" -> addEvent(argument);
        default -> reply(" I don't know that command.");
        }
    }

    /** Prints every task, numbered from 1. */
    private static void showTasks() {
        if (tasks.size() == 0) {
            reply(" Your list is empty. Get after it!");
            return;
        }
        reply(" Here are the tasks in your list:", tasks.toString());
    }

    /**
     * Marks a task as done or not done and confirms the change.
     *
     * @param argument the task number the user typed, as text
     * @param isDone   true to mark as done, false to mark as not done yet
     */
    private static void setDone(String argument, boolean isDone) {
        int taskNumber;
        try {
            taskNumber = Integer.parseInt(argument);
        } catch (NumberFormatException e) {
            // Covers both a missing number ("mark") and a non-number ("mark two").
            reply(" Tell me which task number, e.g. mark 2.");
            return;
        }

        if (!tasks.isValidTaskNumber(taskNumber)) {
            reply(" There's no task " + taskNumber + " in your list.");
            return;
        }

        Task task = isDone ? tasks.mark(taskNumber) : tasks.unmark(taskNumber);
        String message = isDone
                ? " Nice! I've marked this task as done:"
                : " OK, I've marked this task as not done yet:";
        reply(message, "   " + task);
    }

    /**
     * Creates a deadline from {@code <description> /by <when>} and adds it.
     *
     * @param argument everything the user typed after the word "deadline"
     */
    private static void addDeadline(String argument) {
        // Limit of 2 keeps the due time whole even if it contains spaces.
        String[] parts = argument.split(" /by ", 2);
        addTask(new Deadlines(parts[0], parts[1]));
    }

    /**
     * Creates an event from {@code <description> /from <start> /to <end>} and adds it.
     *
     * @param argument everything the user typed after the word "event"
     */
    private static void addEvent(String argument) {
        // One split on either keyword gives description, start and end in order.
        String[] parts = argument.split(" /from | /to ", 3);
        addTask(new Event(parts[0], parts[1], parts[2]));
    }

    /**
     * Adds a task to the list and confirms it, including the new list size.
     *
     * @param task the task to add
     */
    private static void addTask(Task task) {
        tasks.add(task);
        reply(" Got it. I've added this task:",
                "   " + task,
                " Now you have " + tasks.size() + " tasks in the list.");
    }

    /**
     * Prints one reply block: the given lines between two dividers.
     *
     * <p>Every response has this same shape, so keeping it in one place means the
     * layout is defined once rather than repeated in each command.
     *
     * @param lines the lines to print, already spaced as they should appear
     */
    private static void reply(String... lines) {
        System.out.println(DIVIDER);
        for (String line : lines) {
            System.out.println(line);
        }
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
}
