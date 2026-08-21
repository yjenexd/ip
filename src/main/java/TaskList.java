import java.util.ArrayList;

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
     * Adds a task to the end of the list.
     *
     * @param task the task to add
     */
    public void add(Task task) {
        tasks.add(task);
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
     * <p>Callers should check this before calling {@link #mark} or {@link #unmark},
     * which assume a valid number.
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
}
