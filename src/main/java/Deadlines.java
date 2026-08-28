import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/** A task that must be finished by a given time. */
public class Deadlines extends Task {
    private static final DateTimeFormatter DISPLAY_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private final LocalDate by; // yyyy-mm-dd

    /**
     * 
     * @param description
     * @param by accepts yyyy-mm-dd format, e.g. 2023-01-30
     */
    public Deadlines(String description, String by) throws DavidGogginsException {
        super(description);
        try {
            this.by = LocalDate.parse(by, DISPLAY_FORMATTER);
        } catch (DateTimeParseException e) {
            throw new DavidGogginsException("I need the date as yyyy-mm-dd, not \"" + by
                    + "\". Try: deadline return book /by 2019-10-15");
        }
    }

    @Override
    public String toString() {
        return "[D]" + (isDone ? "[X] " : "[ ] ") + description + " (by: " + this.by + ")";
    }

    /** Saved as {@code D | 0 | return book | Sunday}. */
    @Override
    public String toSaveFormat() {
        return "D" + SEPARATOR + doneFlag() + SEPARATOR + description + SEPARATOR + this.by;
    }
}

