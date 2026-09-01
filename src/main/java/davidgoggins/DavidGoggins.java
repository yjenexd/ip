package davidgoggins;

import davidgoggins.parser.Parser;
import davidgoggins.storage.Storage;
import davidgoggins.task.Task;
import davidgoggins.task.TaskList;
import davidgoggins.ui.Ui;

/**
 * Entry point for the David Goggins chatbot.
 *
 * <p>Reads commands until the user types {@code bye}: {@code todo}, {@code deadline},
 * {@code event}, {@code list}, {@code mark <n>} and {@code unmark <n>}. {@link Ui} owns
 * the screen, {@link TaskList} the tasks and {@link Storage} the disk between runs.
 */
public class DavidGoggins {

    /** Where the task list is kept between runs, relative to the folder we run in. */
    private static final String FILE_PATH = "data/tasks.txt";

    /** The command that ends the conversation. */
    private static final String EXIT_COMMAND = "bye";

    /** Handles all reading from and writing to the console. */
    private final Ui ui;

    /** Reads the saved list at start-up and writes it back whenever it changes. */
    private final Storage storage;

    /** The tasks the user has added so far. */
    private final TaskList tasks;

    /**
     * Builds a chatbot that keeps its tasks in the given file.
     *
     * <p>Creates the parts in dependency order: Storage reports a bad save file
     * through the Ui, and TaskList loads itself through the Storage. Loading here
     * rather than in run() prints any save-file warning before the greeting.
     *
     * @param filePath where to keep the saved list, e.g. {@code "data/tasks.txt"}
     */
    public DavidGoggins(String filePath) {
        ui = new Ui();
        // The path is passed in rather than fixed inside Storage, so the one decision
        // about where tasks live is made in the class that assembles the program.
        storage = new Storage(filePath, ui);
        tasks = new TaskList(storage);
    }

    /**
     * Greets the user, shows any tasks restored from the save file, then reads and
     * carries out commands until they type {@code bye} or the input runs out.
     */
    public void run() {
        ui.showWelcome();

        // The tasks were already read from disk by the TaskList built in the
        // constructor, so this only displays them rather than loading them again.
        // Nothing is shown for an empty list: a first run on a new machine should not
        // announce a list the user has not started yet.
        if (tasks.size() > 0) {
            showTasks();
        }

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

    public static void main(String[] args) {
        new DavidGoggins(FILE_PATH).run();
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
    private void handleCommand(String userInput) throws DavidGogginsException {
        String command = Parser.parseCommand(userInput);
        String argument = Parser.parseArgument(userInput);

        switch (command) {
            case "" -> throw new DavidGogginsException("You typed nothing. Give me a command, e.g. list.");
            case "list" -> showTasks();
            case "mark" -> setDone(argument, true); // set the argument (number) task as done
            case "unmark" -> setDone(argument, false); // set the argument (number) task as not done yet
            case "todo" -> addTask(Parser.parseTodo(Parser.rejectSeparator(argument)));
            case "deadline" -> addTask(Parser.parseDeadline(Parser.rejectSeparator(argument)));
            case "event" -> addTask(Parser.parseEvent(Parser.rejectSeparator(argument)));
            case "delete" -> deleteTask(argument);
            default -> throw new DavidGogginsException( // exception message for unknown command
                    "What are you saying! I don't know the command \"" + command + "\". "
                            + "I understand: todo, deadline, event, list, mark, unmark, delete, bye.");
        }
    }

    /** Prints every task, numbered from 1. */
    private void showTasks() {
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
    private void setDone(String argument, boolean isDone) throws DavidGogginsException {
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
    private String taskCount() {
        int count = tasks.size();
        return count + (count == 1 ? " task" : " tasks");
    }

    /**
     * Adds a task to the list and confirms it, including the new list size.
     *
     * @param task the task to add
     */
    private void addTask(Task task) {
        tasks.add(task);
        ui.show(" Got it. I've added this task:",
                "   " + task,
                " Now you have " + taskCount() + " in the list.");
    }

    private void deleteTask(String argument) throws DavidGogginsException {
        if (argument.isEmpty()) {
            throw new DavidGogginsException(
                    "Tell me which task number to delete, e.g. delete 2.");
        }

        int taskNumber = Parser.parseTaskNumber(argument, "delete");

        // A number outside the list is the user's mistake, not a bug, so it is
        // reported the same way as any other bad command.
        if (!tasks.isValidTaskNumber(taskNumber)) {
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
