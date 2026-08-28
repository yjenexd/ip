package davidgoggins.task;

/** A task with only a description and no date attached. */
public class Todo extends Task {

    public Todo(String description) {
        super(description);
    }

    @Override
    public String toString() {
        return "[T]" + (isDone ? "[X] " : "[ ] ") + description;
    }

    /** Saved as {@code T | 1 | read book}. */
    @Override
    public String toSaveFormat() {
        return "T" + SEPARATOR + doneFlag() + SEPARATOR + description;
    }
}
