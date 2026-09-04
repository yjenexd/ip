package davidgoggins.task;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import davidgoggins.DavidGogginsException;

/** A task that must be finished by a given time. */
public class Deadlines extends Task {
    /** The one date format accepted, both when reading input and when printing. */
    private static final DateTimeFormatter DISPLAY_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /**
     * When the task is due.
     *
     * <p>Held as a {@link LocalDate} rather than text, so a nonsense date is rejected
     * once here instead of being stored and printed back at the user unchanged.
     */
    private final LocalDate by;

    /**
     * Creates a deadline that is not done yet.
     *
     * @param description the task's text
     * @param by          when it is due, as {@code yyyy-mm-dd}, e.g. {@code 2023-01-30}
     * @throws DavidGogginsException if {@code by} is not a date in that format
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

    /** Shown as {@code [D][ ] return book (by: 2019-10-15)}. */
    @Override
    public String toString() {
        return "[D]" + (isDone ? "[X] " : "[ ] ") + description + " (by: " + this.by + ")";
    }

    /** Saved as {@code D | 0 | return book | Sunday}. */
    @Override
    public String toSaveFormat() {
        return toSaveFormat("D", by.toString());
    }
}

