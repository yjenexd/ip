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
        // Loaded before the greeting so any warning about the saved file appears
        // before the banner rather than interrupting the conversation later.
        tasks.load();
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

                try {
                    handleCommand(userInput);
                } catch (DavidGogginsException e) {
                    // Every expected problem ends up here, so the error format is
                    // defined once instead of in each command method.
                    reply(" OOPS! " + e.getMessage());
                }
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
     * @throws DavidGogginsException if the command is unknown or its details are wrong
     */
    private static void handleCommand(String userInput) throws DavidGogginsException {
        // Limit of 2 stops the split after the first space, keeping the rest whole.
        String[] parts = userInput.split("\\s+", 2); //split the input into at most two parts
        String command = parts[0].toLowerCase();
        String argument = parts.length > 1 ? parts[1].trim() : "";

        switch (command) {
        case "" -> throw new DavidGogginsException("You typed nothing. Give me a command, e.g. list.");
        case "list" -> showTasks();
        case "mark" -> setDone(argument, true); // set the argument(number) task as done
        case "unmark" -> setDone(argument, false); // set the argument(number) task as not done yet
        case "todo" -> addTodo(rejectSeparator(argument));
        case "deadline" -> addDeadline(rejectSeparator(argument));
        case "event" -> addEvent(rejectSeparator(argument));
        case "delete" -> deleteTask(argument);
        default -> throw new DavidGogginsException( //exception message for unknown command
                "What are you saying! I don't know the command \"" + command + "\". "
                        + "I understand: todo, deadline, event, list, mark, unmark, delete, bye.");
        }
    }

    /**
     * Returns the argument unchanged, or refuses it if it contains the character used
     * to separate fields in the save file.
     *
     * <p>A description such as {@code read book | now} would be written as an extra
     * field and could not be read back, so it is rejected up front rather than being
     * silently mangled. Escaping the character would also work and would accept more
     * input, but it makes both the writer and the reader harder to follow; refusing one
     * rarely used character is the simpler trade for a task list.
     *
     * @param argument everything the user typed after the command word
     * @throws DavidGogginsException if the argument contains the separator character
     */
    private static String rejectSeparator(String argument) throws DavidGogginsException {
        if (argument.contains(Task.SEPARATOR_CHAR)) {
            throw new DavidGogginsException("A task cannot contain the \""
                    + Task.SEPARATOR_CHAR + "\" character, since that is what I use to "
                    + "separate fields when saving. Drop it and try again.");
        }
        return argument;
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
     * @throws DavidGogginsException if the number is missing, not a number, or out of range
     */
    private static void setDone(String argument, boolean isDone) throws DavidGogginsException {
        String commandName = isDone ? "mark" : "unmark";
        if (argument.isEmpty()) {
            throw new DavidGogginsException(
                    "Tell me which task number NOW!, e.g. " + commandName + " 2.");
        }

        int taskNumber;
        try {
            taskNumber = Integer.parseInt(argument);
        } catch (NumberFormatException e) {
            // The user typed something like "mark two" or "mark 2 3".
            throw new DavidGogginsException(
                    "\"" + argument + "\" is not a task number you log! Use a whole number, e.g. "
                            + commandName + " 2.");
        }

        if (!tasks.isValidTaskNumber(taskNumber)) {
            String advice = tasks.size() == 0
                    ? "your list is empty, so add a task first."
                    : "pick a number from 1 to " + tasks.size() + ".";
            throw new DavidGogginsException("There's no task " + taskNumber + " in your list: " + advice);
        }

        Task task = isDone ? tasks.mark(taskNumber) : tasks.unmark(taskNumber);
        String message = isDone
                ? " Nice! I've marked this task as done:"
                : " OK, I've marked this task as not done yet:";
        reply(message, "   " + task);
    }

    /**
     * Creates a todo from its description and adds it.
     *
     * @param argument everything the user typed after the word "todo"
     * @throws DavidGogginsException if the description is empty
     */
    private static void addTodo(String argument) throws DavidGogginsException {
        if (argument.isEmpty()) {
            throw new DavidGogginsException(
                    "The description of a todo cannot be empty you log! Try: todo read book");
        }
        addTask(new Todo(argument));
    }

    /**
     * Creates a deadline from {@code <description> /by <when>} and adds it.
     *
     * @param argument everything the user typed after the word "deadline"
     * @throws DavidGogginsException if the description or the due time is missing
     */
    private static void addDeadline(String argument) throws DavidGogginsException {
        // Splitting on the bare keyword (rather than " /by ") lets us spot a
        // "/by" with nothing after it instead of silently failing to split.
        String[] parts = argument.split("/by", 2);
        if (parts.length < 2) {
            throw new DavidGogginsException(
                    "A deadline needs a /by part you log! Try: deadline return book /by 2026-09-10");
        }

        String description = parts[0].trim();
        String by = parts[1].trim();
        if (description.isEmpty()) {
            throw new DavidGogginsException(
                    "The description of a deadline cannot be empty you log! Try: deadline return book /by 2026-09-10");
        }
        if (by.isEmpty()) {
            throw new DavidGogginsException(
                    "Tell me when it is due after /by you log! Try: deadline return book /by 2026-09-10");
        }
        addTask(new Deadlines(description, by));
    }

    /**
     * Creates an event from {@code <description> /from <start> /to <end>} and adds it.
     *
     * @param argument everything the user typed after the word "event"
     * @throws DavidGogginsException if the description, the start or the end is missing
     */
    private static void addEvent(String argument) throws DavidGogginsException {
        String[] fromParts = argument.split("/from", 2);
        if (fromParts.length < 2) { // user did not provide a /from part
            throw new DavidGogginsException(
                    "An event needs a /from part. Try: event project meeting /from 2026-09-10 /to 2026-09-11");
        }

        String[] toParts = fromParts[1].split("/to", 2);
        if (toParts.length < 2) {
            throw new DavidGogginsException( //user did not provide a /to part
                    "An event needs a /to part after /from. Try: event project meeting /from 2026-09-10 /to 2026-09-11");
        }

        String description = fromParts[0].trim();
        String from = toParts[0].trim();
        String to = toParts[1].trim();
        if (description.isEmpty()) {
            throw new DavidGogginsException( //user did not provide a description
                    "The description of an event cannot be empty. Try: event project meeting /from 2026-09-10 /to 2026-09-11");
        }
        if (from.isEmpty()) {
            throw new DavidGogginsException(  //user did not provide a time after /from
                    "Tell me when the event starts after /from. Try: event project meeting /from 2026-09-10 /to 2026-09-11");
        }
        if (to.isEmpty()) {
            throw new DavidGogginsException( //user did not provide a time after /to
                    "Tell me when the event ends after /to. Try: event project meeting /from 2026-09-10 /to 2026-09-11");
        }
        addTask(new Event(description, from, to));
    }

    /**
     * Returns the list size worded for a sentence: {@code "1 task"} but
     * {@code "2 tasks"}.
     *
     * <p>Both the add and the delete confirmations need this, so it lives in one
     * method rather than being written out (and mis-worded) in each of them.
     */
    private static String taskCount() {
        int count = tasks.size();
        return count + (count == 1 ? " task" : " tasks");
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
                " Now you have " + taskCount() + " in the list.");
    }

    private static void deleteTask(String argument) throws DavidGogginsException {
        if (argument.isEmpty()) {
            throw new DavidGogginsException(
                    "Tell me which task number to delete, e.g. delete 2.");
        }

        int taskNumber;
        try {
            taskNumber = Integer.parseInt(argument);
        } catch (NumberFormatException e) {
            throw new DavidGogginsException( // The user typed something like "delete two" or "delete 2 3".
                    "\"" + argument + "\" is not a task number you log! Use a whole number, e.g. delete 2.");
        }

        if (!tasks.isValidTaskNumber(taskNumber)) { // Check if the task number is valid, if not throw exception
            String advice = tasks.size() == 0
                    ? "your list is empty, so add a task first."
                    : "pick a number from 1 to " + tasks.size() + ".";
            throw new DavidGogginsException("There's no task " + taskNumber + " in your list: " + advice);
        }

        Task removedTask = tasks.remove(taskNumber);
        reply(" Noted. I've removed this task:",
                "   " + removedTask,
                " Now you have " + taskCount() + " in the list.");
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

