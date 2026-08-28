/** A task that must be finished by a given time. */
public class Deadlines extends Task {
    private final String by;

    public Deadlines(String description, String by) {
        super(description);
        this.by = by;
    }

    @Override
    public String toString() {
        return "[D]" + (isDone ? "[X] " : "[ ] ") + description + " (by: " + by + ")";
    }

    /** Saved as {@code D | 0 | return book | Sunday}. */
    @Override
    public String toSaveFormat() {
        return "D" + SEPARATOR + doneFlag() + SEPARATOR + description + SEPARATOR + by;
    }
}
