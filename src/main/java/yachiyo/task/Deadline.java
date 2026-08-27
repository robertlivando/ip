package yachiyo.task;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Represents a task that must be completed by a specific date and time.
 */
public class Deadline extends Task {
    private static final DateTimeFormatter DISPLAY_DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("MMM dd yyyy, h:mm a", Locale.ENGLISH);
    private static final DateTimeFormatter STORAGE_DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("uuuu-MM-dd'T'HH:mm");

    private final LocalDateTime by;

    /**
     * Creates a deadline task with the given description and due date-time.
     *
     * @param description description of the task
     * @param by date and time by which the task must be completed
     */
    public Deadline(String description, LocalDateTime by) {
        super(description);
        this.by = by;
    }

    /**
     * Returns this deadline in the format used by the storage file.
     *
     * @return Stored deadline representation.
     */
    @Override
    public String toFileFormat() {
        return "DEADLINE | " + super.toFileFormat() + " | "
                + this.by.format(STORAGE_DATE_TIME_FORMATTER);
    }

    /**
     * Returns whether this deadline falls on the specified date.
     *
     * @param date Date to check.
     * @return True if the deadline falls on the date; false otherwise.
     */
    @Override
    public boolean occursOn(LocalDate date) {
        return this.by.toLocalDate().equals(date);
    }

    /**
     * Returns this deadline in its user-facing display format.
     *
     * @return Formatted deadline description, status, and due date-time.
     */
    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: "
                + this.by.format(DISPLAY_DATE_TIME_FORMATTER) + ")";
    }
}
