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
}
