package davidgoggins.parser;

import davidgoggins.DavidGogginsException;
import davidgoggins.task.Deadlines;
import davidgoggins.task.Event;
import davidgoggins.task.Task;
import davidgoggins.task.Todo;

/**
 * Deals with making sense of what the user typed.
 *
 * <p>Turns text into something the rest of the program can act on: a command word, a
 * task number, or a ready-built {@link Task}. Nothing here touches the task list or
 * prints anything, so every rule about what counts as valid input is in this one file.
 */
public class Parser {

    // Every method below is static because parsing needs no state: the same input
    // always gives the same result, so there is nothing for an instance to remember.

    /**
     * Returns the command word the user typed, lower-cased.
     *
     * <p>Lower-casing here means {@code LIST} and {@code list} are the same command
     * without every caller having to remember it. The description is left alone, so a
     * task keeps the capitalisation the user gave it.
     *
     * @param userInput the line the user typed, already trimmed
     */
    public static String parseCommand(String userInput) {
        return split(userInput)[0].toLowerCase();
    }

    /**
     * Returns everything the user typed after the command word, trimmed.
     *
     * @param userInput the line the user typed, already trimmed
     * @return the argument, or an empty string if there was none
     */
    public static String parseArgument(String userInput) {
        String[] parts = split(userInput);
        return parts.length > 1 ? parts[1].trim() : "";
    }

    /**
     * Splits the input into the command word and the rest of the line.
     *
     * <p>{@code mark 2} gives {@code "mark"} and {@code "2"}, while a description such
     * as {@code read book} stays intact as a single argument.
     */
    private static String[] split(String userInput) {
        // Limit of 2 stops the split after the first space, keeping the rest whole.
        return userInput.split("\\s+", 2); // split the input into at most two parts
    }

    /**
     * Returns the argument unchanged, or refuses it if it holds the save file's
     * separator character.
     *
     * <p>A description such as {@code read book | now} would be written as an extra
     * field and could not be read back, so it is refused rather than mangled.
     *
     * @param argument everything the user typed after the command word
     * @throws DavidGogginsException if the argument contains the separator character
     */
    public static String rejectSeparator(String argument) throws DavidGogginsException {
        // Escaping the character would accept more input, but it makes both the writer
        // and the reader harder to follow; refusing one rarely used character is the
        // simpler trade for a task list.
        if (argument.contains(Task.SEPARATOR_CHAR)) {
            throw new DavidGogginsException("A task cannot contain the \""
                    + Task.SEPARATOR_CHAR + "\" character, since that is what I use to "
                    + "separate fields when saving. Drop it and try again.");
        }
        return argument;
    }

    /**
     * Turns the number the user typed into an {@code int}.
     *
     * <p>Whether that number actually refers to an existing task is not checked here:
     * that depends on the list, which the parser deliberately knows nothing about.
     *
     * @param argument    the task number the user typed, as text
     * @param commandName the command it was typed for, used in the error message so the
     *                    advice names the command the user actually used
     * @throws DavidGogginsException if the argument is not a whole number
     */
    public static int parseTaskNumber(String argument, String commandName) throws DavidGogginsException {
        try {
            return Integer.parseInt(argument);
        } catch (NumberFormatException e) {
            // The user typed something like "mark two" or "mark 2 3".
            throw new DavidGogginsException(
                    "\"" + argument + "\" is not a task number you log! Use a whole number, e.g. "
                            + commandName + " 2.");
        }
    }

    /**
     * Creates a todo from its description.
     *
     * @param argument everything the user typed after the word "todo"
     * @throws DavidGogginsException if the description is empty
     */
    public static Todo parseTodo(String argument) throws DavidGogginsException {
        if (argument.isEmpty()) {
            throw new DavidGogginsException(
                    "The description of a todo cannot be empty you log! Try: todo read book");
        }
        return new Todo(argument);
    }

    /**
     * Creates a deadline from {@code <description> /by <when>}.
     *
     * @param argument everything the user typed after the word "deadline"
     * @throws DavidGogginsException if the description or the due time is missing
     */
    public static Deadlines parseDeadline(String argument) throws DavidGogginsException {
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
        return new Deadlines(description, by);
    }

    /**
     * Creates an event from {@code <description> /from <start> /to <end>}.
     *
     * @param argument everything the user typed after the word "event"
     * @throws DavidGogginsException if the description, the start or the end is missing
     */
    public static Event parseEvent(String argument) throws DavidGogginsException {
        String[] fromParts = argument.split("/from", 2);
        if (fromParts.length < 2) { // user did not provide a /from part
            throw new DavidGogginsException(
                    "An event needs a /from part. Try: event project meeting /from 2026-09-10 /to 2026-09-11");
        }

        String[] toParts = fromParts[1].split("/to", 2);
        if (toParts.length < 2) {
            throw new DavidGogginsException( // user did not provide a /to part
                    "An event needs a /to part after /from. "
                            + "Try: event project meeting /from 2026-09-10 /to 2026-09-11");
        }

        String description = fromParts[0].trim();
        String from = toParts[0].trim();
        String to = toParts[1].trim();
        if (description.isEmpty()) {
            throw new DavidGogginsException( // user did not provide a description
                    "The description of an event cannot be empty. "
                            + "Try: event project meeting /from 2026-09-10 /to 2026-09-11");
        }
        if (from.isEmpty()) {
            throw new DavidGogginsException( // user did not provide a time after /from
                    "Tell me when the event starts after /from. "
                            + "Try: event project meeting /from 2026-09-10 /to 2026-09-11");
        }
        if (to.isEmpty()) {
            throw new DavidGogginsException( // user did not provide a time after /to
                    "Tell me when the event ends after /to. "
                            + "Try: event project meeting /from 2026-09-10 /to 2026-09-11");
        }
        return new Event(description, from, to);
    }
}
