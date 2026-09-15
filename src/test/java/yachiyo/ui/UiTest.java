package yachiyo.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalTime;

import org.junit.jupiter.api.Test;

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
}
