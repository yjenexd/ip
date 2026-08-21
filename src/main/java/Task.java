/**
 * Represents a single task in the user's list: a description plus whether it is done.
 *
 * <p>A task knows how to render itself as {@code [X] read book} or {@code [ ] read book},
 * so callers never have to build that string themselves.
 */
public abstract class Task {
    protected String description;
    protected boolean isDone;

    public Task(String description) {
        this.description = description;
        this.isDone = false;
    }

    public abstract String toString();

    public void markAsDone() {
        isDone = true;
    }
}