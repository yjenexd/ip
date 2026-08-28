/**
 * Entry point for the David Goggins chatbot.
 *
 * <p>Reads commands from the user until they type {@code bye}. Supported commands
 * are {@code todo}, {@code deadline}, {@code event}, {@code list}, {@code mark <n>}
 * and {@code unmark <n>}.
 *
 * <p>This class works out what each command means and what to say in response. The
 * {@link Ui} it holds decides how that response actually looks on screen, the
 * {@link TaskList} holds the tasks themselves, and the {@link Storage} keeps them on
 * disk between runs.
 */
public class DavidGoggins {

    /** Where the task list is kept between runs, relative to the folder we run in. */
    private static final String FILE_PATH = "data/tasks.txt";

    /** Handles all reading from and writing to the console. */
    private static final Ui ui = new Ui();

    /** Reads the saved list at start-up and writes it back whenever it changes. */
    private static final Storage storage = new Storage(FILE_PATH, ui);

    /**
     * The tasks the user has added so far.
     *
     * <p>Fields are initialised in the order they are written, so the list is loaded --
     * and any warning about the save file printed -- before main() prints the greeting,
     * which is where such a warning belongs.
     */
    private static final TaskList tasks = new TaskList(storage);

    /** The command that ends the conversation. */
    private static final String EXIT_COMMAND = "bye";

    public static void main(String[] args) {
        ui.showWelcome();

        try {
            // hasNextCommand() is false at end of input, so redirected input that
            // omits "bye" exits cleanly instead of throwing.
            while (ui.hasNextCommand()) {
                String userInput = ui.readCommand();

                if (userInput.equalsIgnoreCase(EXIT_COMMAND)) {
                    break;
                }

                try {
                    handleCommand(userInput);
                } catch (DavidGogginsException e) {
                    // Every expected problem ends up here, so the error format is
                    // defined once instead of in each command method.
                    ui.showError(e.getMessage());
                }
            }
        } finally {
            // finally, so the input is released even if a command fails unexpectedly.
            ui.close();
        }

        ui.showFarewell();
    }

    /**
     * Works out which command the user typed and carries it out.
     *
     * <p>The parsing is left to {@link Parser}; what is left here is the decision about
     * which task-list operation each command maps to, and what to say afterwards.
     *
     * @param userInput the line the user typed, already trimmed
     * @throws DavidGogginsException if the command is unknown or its details are wrong
     */
    private static void handleCommand(String userInput) throws DavidGogginsException {
        String command = Parser.parseCommand(userInput);
        String argument = Parser.parseArgument(userInput);

        switch (command) {
        case "" -> throw new DavidGogginsException("You typed nothing. Give me a command, e.g. list.");
        case "list" -> showTasks();
        case "mark" -> setDone(argument, true); // set the argument(number) task as done
        case "unmark" -> setDone(argument, false); // set the argument(number) task as not done yet
        case "todo" -> addTask(Parser.parseTodo(Parser.rejectSeparator(argument)));
        case "deadline" -> addTask(Parser.parseDeadline(Parser.rejectSeparator(argument)));
        case "event" -> addTask(Parser.parseEvent(Parser.rejectSeparator(argument)));
        case "delete" -> deleteTask(argument);
        default -> throw new DavidGogginsException( //exception message for unknown command
                "What are you saying! I don't know the command \"" + command + "\". "
                        + "I understand: todo, deadline, event, list, mark, unmark, delete, bye.");
        }
    }

    /** Prints every task, numbered from 1. */
    private static void showTasks() {
        if (tasks.size() == 0) {
            ui.show(" Your list is empty. Get after it!");
            return;
        }
        ui.show(" Here are the tasks in your list:", tasks.toString());
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

        int taskNumber = Parser.parseTaskNumber(argument, commandName);

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
        ui.show(message, "   " + task);
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
        ui.show(" Got it. I've added this task:",
                "   " + task,
                " Now you have " + taskCount() + " in the list.");
    }

    private static void deleteTask(String argument) throws DavidGogginsException {
        if (argument.isEmpty()) {
            throw new DavidGogginsException(
                    "Tell me which task number to delete, e.g. delete 2.");
        }

        int taskNumber = Parser.parseTaskNumber(argument, "delete");

        if (!tasks.isValidTaskNumber(taskNumber)) { // Check if the task number is valid, if not throw exception
            String advice = tasks.size() == 0
                    ? "your list is empty, so add a task first."
                    : "pick a number from 1 to " + tasks.size() + ".";
            throw new DavidGogginsException("There's no task " + taskNumber + " in your list: " + advice);
        }

        Task removedTask = tasks.remove(taskNumber);
        ui.show(" Noted. I've removed this task:",
                "   " + removedTask,
                " Now you have " + taskCount() + " in the list.");
    }
}
