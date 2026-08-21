public class Event extends Task{
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
    
}
