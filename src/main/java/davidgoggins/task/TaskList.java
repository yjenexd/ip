package davidgoggins.task;

import java.util.ArrayList;
import java.util.List;

import davidgoggins.storage.Storage;

/**
 * Holds the user's tasks in the order they were added.
 *
 * <p>Task numbers used by {@code list}, {@code mark} and {@code unmark} are 1-based,
 * matching what the user sees; the conversion to 0-based {@link ArrayList} indices
 * happens here, so callers only ever deal in the numbers the user typed.
 */
public class TaskList {

    /**
     * ArrayList rather than a plain array because the list grows as the user
     * adds tasks, and we do not know the final size in advance.
     */
    private final ArrayList<Task> tasks;

    /** Used to write the list back to disk whenever it changes. */
    private final Storage storage;

    /**
     * Starts from the tasks already on disk.
     *
     * @param storage where the list is loaded from and saved to
     */
    public TaskList(Storage storage) {
        this.storage = storage;
        this.tasks = new ArrayList<>(storage.load());
    }

    /**
     * Starts from a list given directly, without reading the save file.
     *
     * <p>Provided so this class can be built and tested without touching the disk.
     *
     * @param storage where later changes are saved to
     * @param tasks   the tasks to start with
     */
    public TaskList(Storage storage, List<Task> tasks) {
        this.storage = storage;
        this.tasks = new ArrayList<>(tasks);
    }

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
     *
     * @return the list size, which is 0 before any task is added
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Returns true if {@code taskNumber} refers to an existing task.
     *
     * @param taskNumber the 1-based number the user typed
     * @return true if the number is between 1 and {@link #size()} inclusive
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

    /**
     * Removes the task at the given 1-based position and saves the shortened list.
     *
     * <p>Every later task shifts down one place, so a number the user read from an
     * earlier {@code list} may point at a different task after a delete.
     *
     * @param taskNumber the 1-based number the user typed
     * @return the task that was removed, so the caller can print it back
     */
    public Task remove(int taskNumber) {
        Task task = get(taskNumber);
        tasks.remove(taskNumber - 1);
        save();
        return task;
    }

    /**
     * Writes the current list to disk.
     *
     * <p>Called by every method that changes the list, rather than by the command
     * handlers, so that "the save file matches the list" holds by construction instead
     * of depending on every caller remembering.
     */
    private void save() {
        storage.save(tasks);
    }
}
