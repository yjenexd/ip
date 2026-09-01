package davidgoggins.task;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import davidgoggins.DavidGogginsException;

/**
 * A task that runs from a start date to an end date.
 *
 * <p>Both dates are required and the end may not come before the start, so an event
 * that exists at all covers a sensible range.
 */
public class Event extends Task{
    /** The one date format accepted, both when reading input and when printing. */
    private static final DateTimeFormatter INPUT_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /** When the event starts. */
    private final LocalDate from;

    /** When the event ends, never earlier than {@link #from}. */
    private final LocalDate to;

    /**
     * Creates an event that is not done yet.
     *
     * @param description the task's text
     * @param from        when it starts, as {@code yyyy-mm-dd}, e.g. {@code 2019-10-15}
     * @param to          when it ends, in the same format, and not before {@code from}
     * @throws DavidGogginsException if either date is malformed, or the end is before the start
     */
    public Event(String description, String from, String to) throws DavidGogginsException {
        super(description);
        this.from = parseDate(from);
        this.to = parseDate(to);

        if (this.to.isBefore(this.from)) {
            throw new DavidGogginsException("An event cannot end before it starts: you gave"
                    + " a start of \"" + from + "\" and an end of \"" + to + "\".");
        }
    }

    /** Shown as {@code [E][ ] project meeting (from: 2019-10-15 to: 2019-10-16)}. */
    @Override
    public String toString() {
        return "[E]" + (isDone ? "[X] " : "[ ] ") + description + " (from: " + from + " to: " + to + ")";
    }
    
    /** Saved as {@code E | 0 | project meeting | 2019-10-15 | 2019-10-16}. */
    @Override
    public String toSaveFormat() {
        return "E" + SEPARATOR + doneFlag() + SEPARATOR + description + SEPARATOR + this.from + SEPARATOR + this.to;
    }

    /**
     * Turns one of the two date fields into a {@link LocalDate}.
     *
     * <p>Static because it needs nothing from the object, which also lets the
     * constructor call it while the {@code final} fields are still being assigned.
     *
     * @param date the text the user typed, expected as {@code yyyy-mm-dd}
     * @return the date it describes
     * @throws DavidGogginsException if the text is not a date in that format
     */
    private static LocalDate parseDate(String date) throws DavidGogginsException {
        try {
            return LocalDate.parse(date, INPUT_FORMATTER);
        } catch (DateTimeParseException e) {
            throw new DavidGogginsException("I need the date as yyyy-mm-dd, not \"" + date
                    + "\". Try: event project meeting /from 2019-10-15 /to 2019-10-16");
        }
    }
}
