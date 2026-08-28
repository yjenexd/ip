/** A task that runs from one time to another. */
public class Event extends Task {
    private final String from;
    private final String to;

    public Event(String description, String from, String to) {
        super(description);
        this.from = from;
        this.to = to;
    }

    @Override
    public String toString() {
        return "[E]" + (isDone ? "[X] " : "[ ] ") + description + " (from: " + from + " to: " + to + ")";
    }

    /** Saved as {@code E | 0 | project meeting | Mon 2pm | 4pm}. */
    @Override
    public String toSaveFormat() {
        return "E" + SEPARATOR + doneFlag() + SEPARATOR + description
                + SEPARATOR + from + SEPARATOR + to;
    }
}
