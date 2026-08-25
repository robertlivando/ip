import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Represents a task that must be completed by a specific date.
 */
public class Deadline extends Task {
    private static final DateTimeFormatter DISPLAY_DATE_FORMATTER =
            DateTimeFormatter.ofPattern("MMM dd yyyy", Locale.ENGLISH);

    private final LocalDate by;

    /**
     * Creates a deadline task with the given description and due date.
     *
     * @param description description of the task
     * @param by date by which the task must be completed
     */
    public Deadline(String description, LocalDate by) {
        super(description);
        this.by = by;
    }

    @Override
    public String toFileFormat() {
        return "DEADLINE | " + super.toFileFormat() + " | "
                + this.by.format(DateTimeFormatter.ISO_LOCAL_DATE);
    }

    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: "
                + this.by.format(DISPLAY_DATE_FORMATTER) + ")";
    }
}
