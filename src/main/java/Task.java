/**
 * Represents a single task in the user's list: a description plus whether it is done.
 *
 * <p>A task knows how to render itself as {@code [X] read book} or {@code [ ] read book},
 * so callers never have to build that string themselves.
 */
public class Task {
    
    private final String description;
    private boolean isDone;

    /**
     * Creates a task that starts off as not done.
     *
     * @param description what the user has to do
     */
    public Task(String description) {
        this.description = description;
        this.isDone = false;
    }

    public String getDescription() {
        return description;
    }

    public boolean isDone() {
        return isDone;
    }

    /** Marks this task as done. */
    public void markAsDone() {
        this.isDone = true;
    }

    /** Marks this task as not done yet. */
    public void markAsNotDone() {
        this.isDone = false;
    }

    /**
     * Returns the character shown inside the status box: {@code X} when done,
     * a single space otherwise, so that both forms are the same width.
     *
     * @return the status icon
     */
    private String getStatusIcon() {
        return isDone ? "X" : " ";
    }

    /**
     * Returns this task in the display form {@code [X] read book}.
     *
     * <p>Overriding {@code toString()} means a Task can be dropped straight into
     * string concatenation or {@code println} and print correctly.
     */
    @Override
    public String toString() {
        return "[" + getStatusIcon() + "] " + description;
    }
}
