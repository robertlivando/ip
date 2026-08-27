package yachiyo.task;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Represents an event that takes place between a start and end date-time.
 */
public class Event extends Task {
    private static final DateTimeFormatter DISPLAY_DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("MMM dd yyyy, h:mm a", Locale.ENGLISH);
    private static final DateTimeFormatter STORAGE_DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("uuuu-MM-dd'T'HH:mm");

    private final LocalDateTime from;
    private final LocalDateTime to;

    /**
     * Creates an event with the given description, start date-time, and end date-time.
     *
     * @param description description of the event.
     * @param from date and time when the event starts.
     * @param to date and time when the event ends.
     */
    public Event(String description, LocalDateTime from, LocalDateTime to) {
        super(description);
        this.from = from;
        this.to = to;
    }

    /**
     * Returns this event in the format used by the storage file.
     *
     * @return Stored event representation.
     */
    @Override
    public String toFileFormat() {
        return String.format("EVENT | %s | %s | %s", super.toFileFormat(),
                this.from.format(STORAGE_DATE_TIME_FORMATTER),
                this.to.format(STORAGE_DATE_TIME_FORMATTER));
    }

    @Override
    public boolean occursOn(LocalDate date) {
        LocalDate startDate = this.from.toLocalDate();
        LocalDate endDate = this.to.toLocalDate();
        return !date.isBefore(startDate) && !date.isAfter(endDate);
    }

    /**
     * Returns this event in its user-facing display format.
     *
     * @return Formatted event description, status, and duration.
     */
    @Override
    public String toString() {
        return String.format("[E]%s (from: %s, to: %s)", super.toString(),
                this.from.format(DISPLAY_DATE_TIME_FORMATTER),
                this.to.format(DISPLAY_DATE_TIME_FORMATTER));
    }
}
