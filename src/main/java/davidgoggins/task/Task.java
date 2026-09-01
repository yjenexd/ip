package davidgoggins.task;

import davidgoggins.DavidGogginsException;

/**
 * Represents a single task in the user's list: a description plus whether it is done.
 *
 * <p>Renders itself two ways: {@link #toString()} for display, e.g.
 * {@code [T][X] read book}, and {@link #toSaveFormat()} for disk, e.g.
 * {@code T | 1 | read book}, whose separate fields make loading a split, not a hunt.
 */
public abstract class Task {
    /**
     * Separates the fields of a saved line.
     *
     * <p>Spaces are included so saved files stay readable. Every field is trimmed on
     * load, so an extra space either side of the bar does no harm.
     */
    public static final String SEPARATOR = " | ";

    /**
     * The character the separator is built from, kept apart from {@link #SEPARATOR} so
     * user input can be checked for it. A description containing this character would
     * produce an extra field on save and be unreadable on load, so it is rejected.
     */
    public static final String SEPARATOR_CHAR = "|";

    /** The task's text, kept exactly as the user typed it. */
    protected String description;

    /** Whether the user has ticked this task off. */
    protected boolean isDone;

    /**
     * Creates a task that is not done yet.
     *
     * @param description the task's text, which must not contain {@link #SEPARATOR_CHAR}
     */
    public Task(String description) {
        this.description = description;
        this.isDone = false;
    }

    /**
     * Returns this task as one line for display, e.g. {@code [T][X] read book}.
     *
     * <p>Abstract because only the subclass knows its type letter and its extra fields.
     *
     * @return the line to show the user, without a line separator
     */
    public abstract String toString();

    /**
     * Returns this task as one line of the save file.
     *
     * <p>Each subclass writes its own type marker and its own extra fields, which is why
     * this is abstract rather than implemented once here: only the subclass knows how
     * many fields it has.
     *
     * @return the line to write, without a line separator
     */
    public abstract String toSaveFormat();

    /**
     * Rebuilds a task from one line of the save file.
     *
     * <p>The reverse of {@link #toSaveFormat()}, but {@code static}: when a line is read
     * there is no object yet to call a method on, so this factory inspects the type
     * marker and decides which constructor to call.
     *
     * @param line one line from the save file
     * @return the task the line describes
     * @throws DavidGogginsException if the line is not in the expected format
     */
    public static Task fromSaveFormat(String line) throws DavidGogginsException {
        // -1 keeps trailing empty fields, so "D | 0 | return book | " is spotted as a
        // missing due date instead of silently losing the field.
        String[] fields = line.split("\\" + SEPARATOR_CHAR, -1);
        for (int i = 0; i < fields.length; i++) {
            fields[i] = fields[i].trim();
        }

        if (fields.length < 3) {
            throw new DavidGogginsException("expected at least 3 fields, found " + fields.length);
        }

        String type = fields[0];
        boolean isDone = parseDoneFlag(fields[1]);
        String description = requireNonEmpty(fields[2], "description");

        Task task = switch (type) {
            case "T" -> {
                requireFieldCount(fields, 3, "todo");
                yield new Todo(description);
            }
            case "D" -> {
                requireFieldCount(fields, 4, "deadline");
                yield new Deadlines(description, requireNonEmpty(fields[3], "due date"));
            }
            case "E" -> {
                requireFieldCount(fields, 5, "event");
                yield new Event(description,
                        requireNonEmpty(fields[3], "start time"),
                        requireNonEmpty(fields[4], "end time"));
            }
            default -> throw new DavidGogginsException("unknown task type \"" + type + "\"");
        };

        // The constructors always start a task as not done, so the saved flag is
        // applied afterwards through the same methods the mark/unmark commands use.
        if (isDone) {
            task.markAsDone();
        }
        return task;
    }

    /** Turns the saved {@code 1}/{@code 0} flag back into a boolean. */
    private static boolean parseDoneFlag(String field) throws DavidGogginsException {
        return switch (field) {
            case "1" -> true;
            case "0" -> false;
            default -> throw new DavidGogginsException(
                    "done flag should be 0 or 1, found \"" + field + "\"");
        };
    }

    /** Rejects a field that is present but blank, which would give a task with no text. */
    private static String requireNonEmpty(String field, String name) throws DavidGogginsException {
        if (field.isEmpty()) {
            throw new DavidGogginsException("the " + name + " is empty");
        }
        return field;
    }

    /** Rejects a line with the wrong number of fields for its type. */
    private static void requireFieldCount(String[] fields, int expected, String type)
            throws DavidGogginsException {
        if (fields.length != expected) {
            throw new DavidGogginsException("a " + type + " needs " + expected
                    + " fields, found " + fields.length);
        }
    }

    /**
     * Returns the saved done flag.
     *
     * @return {@code "1"} when the task is done, {@code "0"} when it is not
     */
    protected String doneFlag() {
        return isDone ? "1" : "0";
    }

    /**
     * Returns true if this task's description contains the given keyword.
     *
     * <p>Matching ignores case, so {@code find book} also finds a task the user
     * wrote as {@code Read Book}. Asking the task itself, rather than handing out
     * the description through a getter, keeps the field private to this class.
     *
     * @param keyword the text to look for
     * @return true if the description contains the keyword
     */
    public boolean matches(String keyword) {
        return description.toLowerCase().contains(keyword.toLowerCase());
    }

    public void markAsDone() {
        isDone = true;
    }

    /** Marks this task as not done yet, used by the {@code unmark} command. */
    public void markAsNotDone() {
        isDone = false;
    }
}
