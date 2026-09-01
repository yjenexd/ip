package davidgoggins.task;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import davidgoggins.DavidGogginsException;

public class Event extends Task {
    private static final DateTimeFormatter INPUT_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private final LocalDate from;
    private final LocalDate to;

    public Event(String description, String from, String to) throws DavidGogginsException {
        super(description);
        this.from = parseDate(from);
        this.to = parseDate(to);

        if (this.to.isBefore(this.from)) {
            throw new DavidGogginsException("An event cannot end before it starts: you gave"
                    + " a start of \"" + from + "\" and an end of \"" + to + "\".");
        }
    }

    @Override
    public String toString() {
        return "[E]" + (isDone ? "[X] " : "[ ] ") + description + " (from: " + from + " to: " + to + ")";
    }

    @Override
    public String toSaveFormat() {
        return "E" + SEPARATOR + doneFlag() + SEPARATOR + description + SEPARATOR + this.from + SEPARATOR + this.to;
    }

    private static LocalDate parseDate(String date) throws DavidGogginsException {
        try {
            return LocalDate.parse(date, INPUT_FORMATTER);
        } catch (DateTimeParseException e) {
            throw new DavidGogginsException("I need the date as yyyy-mm-dd, not \"" + date
                    + "\". Try: event project meeting /from 2019-10-15 /to 2019-10-16");
        }
    }
}
