package yachiyo.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.StringWriter;
import java.time.LocalTime;

import org.junit.jupiter.api.Test;

import yachiyo.task.ToDo;

public class UiTest {
    private static final String GREETING_SUFFIX =
            "! Yachiyo here!\nWhat shall we accomplish today?";

    @Test
    public void getGreeting_morning_ohayouReturned() {
        assertEquals("Ohayou" + GREETING_SUFFIX, Ui.getGreeting(LocalTime.of(11, 59)));
    }

    @Test
    public void getGreeting_afternoon_konnichiwaReturned() {
        assertEquals("Konnichiwa" + GREETING_SUFFIX, Ui.getGreeting(LocalTime.NOON));
        assertEquals("Konnichiwa" + GREETING_SUFFIX, Ui.getGreeting(LocalTime.of(17, 59)));
    }

    @Test
    public void getGreeting_evening_konbanwaReturned() {
        assertEquals("Konbanwa" + GREETING_SUFFIX, Ui.getGreeting(LocalTime.of(18, 0)));
    }

    @Test
    public void showTaskMarked_noTasksRemaining_celebrationReturned() {
        StringWriter output = new StringWriter();

        try (Ui ui = new Ui(output)) {
            ui.showTaskMarked(new ToDo("Read book"), 0);
        }

        assertTrue(output.toString().contains(
                "Yayyy! Everything in our lineup is complete!🥳🎉"
                        + System.lineSeparator() + "Good job!"));
    }
}
