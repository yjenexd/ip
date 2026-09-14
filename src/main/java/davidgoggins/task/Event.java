package davidgoggins.task;

import java.time.LocalDate;

import davidgoggins.DavidGogginsException;

/**
 * A task that runs from a start date to an end date.
 *
 * <p>Both dates are required and the end may not come before the start, so an event
 * that exists at all covers a sensible range.
 */
public class Event extends Task {
    /** A correct event command, suggested when a date cannot be read. */
    private static final String EXAMPLE = "event project meeting /from 2019-10-15 /to 2019-10-16";

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
        this.from = parseDate(from, EXAMPLE);
        this.to = parseDate(to, EXAMPLE);

        if (this.to.isBefore(this.from)) {
            throw new DavidGogginsException("An event cannot end before it starts: you gave"
                    + " a start of \"" + from + "\" and an end of \"" + to + "\".");
        }
    }

    /** Shown as {@code [E][ ] project meeting (from: 2019-10-15 to: 2019-10-16)}. */
    @Override
    public String toString() {
        return "[E]" + getStatusIcon() + description + " (from: " + from + " to: " + to + ")";
    }

    /** Saved as {@code E | 0 | project meeting | 2019-10-15 | 2019-10-16}. */
    @Override
    public String toSaveFormat() {
        return toSaveFormat("E", from.toString(), to.toString());
    }
}
