package yachiyo.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

/**
 * Tests date matching and formatting behavior of {@link Deadline} tasks.
 */
public class DeadlineTest {
    private static final LocalDateTime DUE_DATE_TIME =
            LocalDateTime.of(2026, 8, 20, 17, 0);

    private final Deadline deadline = new Deadline("Submit report", DUE_DATE_TIME);

    @Test
    public void occursOn_dateBeforeDeadline_falseReturned() {
        assertFalse(deadline.occursOn(LocalDate.of(2026, 8, 19)));
    }

    @Test
    public void occursOn_deadlineDate_trueReturned() {
        assertTrue(deadline.occursOn(DUE_DATE_TIME.toLocalDate()));
    }

    @Test
    public void occursOn_dateAfterDeadline_falseReturned() {
        assertFalse(deadline.occursOn(LocalDate.of(2026, 8, 21)));
    }

    @Test
    public void toFileFormat_incompleteDeadline_correctFormatReturned() {
        assertEquals(
                "DEADLINE | 0 | Submit report | 2026-08-20T17:00",
                deadline.toFileFormat()
        );
    }

    @Test
    public void toFileFormat_completedDeadline_correctFormatReturned() {
        deadline.markAsDone();

        assertEquals(
                "DEADLINE | 1 | Submit report | 2026-08-20T17:00",
                deadline.toFileFormat()
        );
    }

    @Test
    public void toString_incompleteDeadline_correctDisplayReturned() {
        assertEquals(
                "[D][ ] Submit report (by: Aug 20 2026, 5:00 PM)",
                deadline.toString()
        );
    }

    @Test
    public void toString_completedDeadline_correctDisplayReturned() {
        deadline.markAsDone();

        assertEquals(
                "[D][X] Submit report (by: Aug 20 2026, 5:00 PM)",
                deadline.toString()
        );
    }
}
