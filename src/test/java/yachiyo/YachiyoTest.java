package yachiyo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class YachiyoTest {
    @TempDir
    private Path temporaryDirectory;

    @Test
    public void getResponse_addThenList_taskStoredAndShown() {
        Path dataFilePath = temporaryDirectory.resolve("yachiyo.txt");
        Yachiyo yachiyo = new Yachiyo(dataFilePath);

        String addResponse = yachiyo.getResponse("todo prepare slides");
        String listResponse = yachiyo.getResponse("list");

        assertTrue(addResponse.contains("I've added this to our lineup"));
        assertTrue(addResponse.contains("[T][ ] prepare slides"));
        assertTrue(listResponse.contains("1. [T][ ] prepare slides"));
    }

    @Test
    public void getResponse_newInstance_taskLoadedFromStorage() {
        Path dataFilePath = temporaryDirectory.resolve("yachiyo.txt");
        new Yachiyo(dataFilePath).getResponse("todo preserve this task");

        String response = new Yachiyo(dataFilePath).getResponse("list");

        assertTrue(response.contains("1. [T][ ] preserve this task"));
    }

    @Test
    public void getResponse_editDescription_taskUpdatedAndStored() {
        Path dataFilePath = temporaryDirectory.resolve("yachiyo.txt");
        Yachiyo yachiyo = new Yachiyo(dataFilePath);
        yachiyo.getResponse("todo prepare slides");

        String editResponse = yachiyo.getResponse(
                "edit 1 /description prepare presentation slides"
        );
        String storedTaskResponse = new Yachiyo(dataFilePath).getResponse("list");

        assertTrue(editResponse.contains("I've updated this task"));
        assertTrue(editResponse.contains("[T][ ] prepare presentation slides"));
        assertTrue(storedTaskResponse.contains("1. [T][ ] prepare presentation slides"));
    }

    @Test
    public void getResponse_editDeadline_taskUpdatedAndStored() {
        Path dataFilePath = temporaryDirectory.resolve("yachiyo.txt");
        Yachiyo yachiyo = new Yachiyo(dataFilePath);
        yachiyo.getResponse("deadline submit report /by 20/9/2026 1700");

        String editResponse = yachiyo.getResponse("edit 1 /by 21/9/2026 1800");
        String storedTaskResponse = new Yachiyo(dataFilePath).getResponse("list");

        assertTrue(editResponse.contains("I've updated this task"));
        assertTrue(editResponse.contains("by: Sep 21 2026, 6:00 PM"));
        assertTrue(storedTaskResponse.contains("by: Sep 21 2026, 6:00 PM"));
    }

    @Test
    public void getResponse_invalidCommand_errorReturned() {
        Yachiyo yachiyo = new Yachiyo(temporaryDirectory.resolve("yachiyo.txt"));

        String response = yachiyo.getResponse("dance");

        assertEquals("Oh? I don’t recognize that command just yet. Could you try another one?", response);
    }

    @Test
    public void getGreeting_guiOpened_introductionReturned() {
        Yachiyo yachiyo = new Yachiyo(temporaryDirectory.resolve("yachiyo.txt"));

        String greeting = yachiyo.getGreeting();

        assertEquals("Hello! Yachiyo here!\nWhat shall we accomplish today?", greeting);
    }

    @Test
    public void getResponse_bye_farewellReturnedWithoutBreaker() {
        Yachiyo yachiyo = new Yachiyo(temporaryDirectory.resolve("yachiyo.txt"));

        String response = yachiyo.getResponse("bye");

        assertEquals("Until we meet again. Take care!~", response);
        assertTrue(yachiyo.isExitRequested());
    }
}
