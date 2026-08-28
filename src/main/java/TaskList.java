import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Holds the user's tasks in the order they were added.
 *
 * <p>Task numbers used by the {@code list}, {@code mark} and {@code unmark} commands
 * are 1-based, matching what the user sees. This class does the conversion to the
 * 0-based {@link ArrayList} indices internally, so callers only ever deal in the
 * numbers the user typed.
 */
public class TaskList {

    /**
     * ArrayList rather than a plain array because the list grows as the user
     * adds tasks, and we do not know the final size in advance.
     */
    private final ArrayList<Task> tasks = new ArrayList<>();

    /**
     * Where the list is saved between runs.
     *
     * <p>Built with {@link Path#of} rather than a string such as {@code "data/tasks.txt"}
     * so the correct separator is used on every OS, and kept relative to the folder the
     * program is run from so it works on any machine.
     */
    private final Path file = Path.of("data", "tasks.txt");

    // No constructor is declared, so Java supplies an empty one. The folder is not
    // created here: a run that never changes the list should not leave a stray
    // ./data/ folder behind. save() creates it at the moment it is actually needed.

    /**
     * Adds a task to the end of the list.
     *
     * @param task the task to add
     */
    public void add(Task task) {
        tasks.add(task);
        save();
    }

    /**
     * Returns the number of tasks currently stored.
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Returns true if {@code taskNumber} refers to an existing task.
     *
     *
     * @param taskNumber the 1-based number the user typed
     */
    public boolean isValidTaskNumber(int taskNumber) {
        return taskNumber >= 1 && taskNumber <= tasks.size();
    }

    /**
     * Returns the task at the given 1-based position.
     *
     * @param taskNumber the 1-based number the user typed
     * @return the task at that position
     */
    public Task get(int taskNumber) {
        return tasks.get(taskNumber - 1); // -1 converts the user's numbering to ArrayList's
    }

    /**
     * Marks the given task as done.
     *
     * @param taskNumber the 1-based number the user typed
     * @return the task that was marked, so the caller can print it back
     */
    public Task mark(int taskNumber) {
        Task task = get(taskNumber);
        task.markAsDone();
        save();
        return task;
    }

    /**
     * Marks the given task as not done yet.
     *
     * @param taskNumber the 1-based number the user typed
     * @return the task that was unmarked, so the caller can print it back
     */
    public Task unmark(int taskNumber) {
        Task task = get(taskNumber);
        task.markAsNotDone();
        save();
        return task;
    }

    /**
     * Returns the numbered list of tasks, one per line, in the display form
     * {@code  1.[X] read book}.
     *
     * <p>The header line ("Here are the tasks in your list:") is left to the caller,
     * since it is part of the conversation rather than part of the list itself.
     */
    @Override
    public String toString() {

        StringBuilder lines = new StringBuilder();
        for (int i = 0; i < tasks.size(); i++) {
            if (i > 0) {
                lines.append(System.lineSeparator());
            }
            // i + 1 converts back to the 1-based numbering the user sees.
            lines.append(" ").append(i + 1).append(".").append(tasks.get(i));
        }
        return lines.toString();
    }

    public Task remove(int taskNumber) {
        Task task = get(taskNumber);
        tasks.remove(taskNumber - 1);
        save();
        return task;
    }

    /**
     * Writes the whole list to ./data/tasks.txt, replacing whatever was there before.
     *
     * <p>The entire file is rewritten on every change rather than appending one line,
     * because {@code delete}, {@code mark} and {@code unmark} alter lines that are
     * already written, and a file has no "replace line 2" operation.
     */
    private void save() {
        StringBuilder lines = new StringBuilder();
        for (Task task : tasks) {
            lines.append(task.toSaveFormat()).append(System.lineSeparator());
        }

        try {
            // createDirectories (plural) makes any missing parent folders and does
            // nothing if they already exist, unlike createDirectory which throws.
            Path parent = file.getParent();
            if (parent != null) { // null only if the path were a bare file name
                Files.createDirectories(parent);
            }
            Files.writeString(file, lines.toString());
        } catch (IOException e) {
            // Saving is a background chore, so a failure warns the user but does not
            // stop the command they asked for from succeeding in memory.
            System.out.println(" Warning: could not save your tasks (" + e.getMessage() + ").");
        }
    }

    /**
     * Loads any previously saved tasks into this list.
     *
     * <p>Called once at start-up. Three situations are handled separately because the
     * right response differs for each:
     * <ul>
     *   <li><b>No file yet</b> — the normal first run on a new machine. Start empty, say nothing.</li>
     *   <li><b>File unreadable</b> — warn, then start empty rather than refusing to run.</li>
     *   <li><b>A line is corrupted</b> — skip that line, keep the good ones, and warn.</li>
     * </ul>
     */
    public void load() {
        List<String> lines;
        try {
            lines = Files.readAllLines(file);
        } catch (NoSuchFileException e) {
            // Expected on a first run, so this is not worth telling the user about.
            // Caught before IOException because it is a subclass of it.
            return;
        } catch (IOException e) {
            // Covers an unreadable file, a folder where the file should be, and so on.
            System.out.println(" Warning: could not read your saved tasks ("
                    + e.getMessage() + "). Starting with an empty list.");
            return;
        }

        int skipped = 0;
        for (String line : lines) {
            if (line.isBlank()) {
                continue; // a stray blank line is harmless, not corruption
            }
            try {
                // tasks.add, not this.add: add() saves, and rewriting the file part-way
                // through loading would delete the corrupted lines before the user is told.
                tasks.add(Task.fromSaveFormat(line));
            } catch (DavidGogginsException e) {
                skipped++;
            }
        }

        if (skipped > 0) {
            System.out.println(" Warning: skipped " + skipped + " unreadable line"
                    + (skipped == 1 ? "" : "s") + " in " + file
                    + ". They will be dropped the next time the list changes.");
        }
    }

}
