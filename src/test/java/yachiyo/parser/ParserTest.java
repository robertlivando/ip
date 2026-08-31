package yachiyo.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;

import yachiyo.command.AddCommand;
import yachiyo.command.Command;
import yachiyo.command.DeleteCommand;
import yachiyo.command.ExitCommand;
import yachiyo.command.FindCommand;
import yachiyo.command.FindOnDateCommand;
import yachiyo.command.ListCommand;
import yachiyo.command.MarkCommand;
import yachiyo.command.UnmarkCommand;
import yachiyo.exception.YachiyoException;
import yachiyo.storage.Storage;
import yachiyo.task.Deadline;
import yachiyo.task.Event;
import yachiyo.task.Task;
import yachiyo.task.TaskList;
import yachiyo.task.ToDo;
import yachiyo.ui.Ui;

/**
 * Tests conversion of user input into executable commands.
 */
public class ParserTest {
    private final SilentUi ui = new SilentUi();
    private final Storage storage = new NoOpStorage();

    @Test
    public void parse_listCommand_listCommandReturned() throws YachiyoException {
        assertInstanceOf(ListCommand.class, Parser.parse("list"));
    }

    @Test
    public void parse_byeCommand_exitCommandReturned() throws YachiyoException {
        assertInstanceOf(ExitCommand.class, Parser.parse("bye"));
    }

    @Test
    public void parse_findCommand_findCommandReturned() throws YachiyoException {
        assertInstanceOf(FindCommand.class, Parser.parse("find book"));
    }

    @Test
    public void parse_markCommand_taskNumberPassed() throws YachiyoException {
        Task firstTask = new ToDo("Read book");
        Task secondTask = new ToDo("Return book");
        TaskList tasks = new TaskList(List.of(firstTask, secondTask));
        Command command = Parser.parse("mark 2");

        assertInstanceOf(MarkCommand.class, command);
        command.execute(tasks, ui, storage);
        assertFalse(firstTask.isCompleted());
        assertTrue(secondTask.isCompleted());
    }

    @Test
    public void parse_unmarkCommand_taskNumberPassed() throws YachiyoException {
        Task firstTask = new ToDo("Read book");
        Task secondTask = new ToDo("Return book");
        secondTask.markAsDone();
        TaskList tasks = new TaskList(List.of(firstTask, secondTask));
        Command command = Parser.parse("unmark 2");

        assertInstanceOf(UnmarkCommand.class, command);
        command.execute(tasks, ui, storage);
        assertFalse(secondTask.isCompleted());
    }

    @Test
    public void parse_deleteCommand_taskNumberPassed() throws YachiyoException {
        Task firstTask = new ToDo("Read book");
        TaskList tasks = new TaskList(List.of(firstTask, new ToDo("Return book")));
        Command command = Parser.parse("delete 2");

        assertInstanceOf(DeleteCommand.class, command);
        command.execute(tasks, ui, storage);
        assertEquals(List.of(firstTask), tasks.getTasks());
    }

    @Test
    public void parse_todoCommand_descriptionPassed() throws YachiyoException {
        Task task = executeAddCommand("  ToDo   Read book  ");

        assertInstanceOf(ToDo.class, task);
        assertEquals("Read book", task.getDescription());
    }

    @Test
    public void parse_deadlineCommand_detailsPassed() throws YachiyoException {
        Task task = executeAddCommand("deadline Submit report /by 20/8/2026 1700");

        assertInstanceOf(Deadline.class, task);
        assertEquals(
                "DEADLINE | 0 | Submit report | 2026-08-20T17:00",
                task.toFileFormat()
        );
    }

    @Test
    public void parse_eventCommand_detailsPassed() throws YachiyoException {
        Task task = executeAddCommand(
                "event Orientation /from 20/8/2026 0900 /to 22/8/2026 1700"
        );

        assertInstanceOf(Event.class, task);
        assertEquals(
                "EVENT | 0 | Orientation | 2026-08-20T09:00 | 2026-08-22T17:00",
                task.toFileFormat()
        );
    }

    @Test
    public void parse_onCommand_datePassed() throws YachiyoException {
        LocalDate expectedDate = LocalDate.of(2026, 8, 20);
        Command command = Parser.parse("on 20/8/2026");

        assertInstanceOf(FindOnDateCommand.class, command);
        command.execute(new TaskList(), ui, storage);
        assertEquals(expectedDate, ui.lastDateShown);
    }

    @Test
    public void parse_unknownCommand_exceptionThrown() {
        assertThrows(YachiyoException.class, () -> Parser.parse("unknown"));
    }

    @Test
    public void parse_taskNumberMissing_exceptionThrown() {
        assertThrows(YachiyoException.class, () -> Parser.parse("mark"));
    }

    @Test
    public void parse_findKeywordMissing_exceptionThrown() {
        assertThrows(YachiyoException.class, () -> Parser.parse("find"));
    }

    @Test
    public void parse_taskNumberNotWholeNumber_exceptionThrown() {
        assertThrows(YachiyoException.class, () -> Parser.parse("delete two"));
    }

    @Test
    public void parse_todoDescriptionMissing_exceptionThrown() {
        assertThrows(YachiyoException.class, () -> Parser.parse("todo"));
    }

    @Test
    public void parse_deadlineDescriptionMissing_exceptionThrown() {
        assertThrows(YachiyoException.class, () -> Parser.parse("deadline /by 20/8/2026 1700"));
    }

    @Test
    public void parse_deadlineDateTimeMissing_exceptionThrown() {
        assertThrows(YachiyoException.class, () -> Parser.parse("deadline Submit report"));
    }

    @Test
    public void parse_deadlineDateTimeInvalid_exceptionThrown() {
        assertThrows(YachiyoException.class, () -> Parser.parse("deadline Submit report /by 31/2/2026 1700"));
    }

    @Test
    public void parse_eventDescriptionMissing_exceptionThrown() {
        assertThrows(YachiyoException.class, () -> Parser.parse(
                "event /from 20/8/2026 0900 /to 20/8/2026 1700"
        ));
    }

    @Test
    public void parse_eventStartMissing_exceptionThrown() {
        assertThrows(YachiyoException.class, () -> Parser.parse("event Orientation"));
    }

    @Test
    public void parse_eventEndMissing_exceptionThrown() {
        assertThrows(YachiyoException.class, () -> Parser.parse(
                "event Orientation /from 20/8/2026 0900"
        ));
    }

    @Test
    public void parse_eventStartInvalid_exceptionThrown() {
        assertThrows(YachiyoException.class, () -> Parser.parse(
                "event Orientation /from tomorrow /to 20/8/2026 1700"
        ));
    }

    @Test
    public void parse_eventEndInvalid_exceptionThrown() {
        assertThrows(YachiyoException.class, () -> Parser.parse(
                "event Orientation /from 20/8/2026 0900 /to later"
        ));
    }

    @Test
    public void parse_eventEndNotAfterStart_exceptionThrown() {
        assertThrows(YachiyoException.class, () -> Parser.parse(
                "event Orientation /from 20/8/2026 1700 /to 20/8/2026 1700"
        ));
    }

    @Test
    public void parse_onDateMissing_exceptionThrown() {
        assertThrows(YachiyoException.class, () -> Parser.parse("on"));
    }

    @Test
    public void parse_onDateInvalid_exceptionThrown() {
        assertThrows(YachiyoException.class, () -> Parser.parse("on 31/2/2026"));
    }

    /**
     * Parses and executes an add command, returning the task it created.
     *
     * @param input add command to parse.
     * @return task added by the parsed command.
     * @throws YachiyoException if parsing or execution fails.
     */
    private Task executeAddCommand(String input) throws YachiyoException {
        Command command = Parser.parse(input);
        assertInstanceOf(AddCommand.class, command);
        TaskList tasks = new TaskList();

        command.execute(tasks, ui, storage);

        return tasks.get(1);
    }

    /**
     * UI test double that suppresses output and records searched dates.
     */
    private static final class SilentUi extends Ui {
        private LocalDate lastDateShown;

        @Override
        public void showTaskAdded(Task task, int totalCount) {
            // No output is needed while testing parsing.
        }

        @Override
        public void showTaskDeleted(Task task, int totalCount) {
            // No output is needed while testing parsing.
        }

        @Override
        public void showTaskMarked(Task task, int remainingCount) {
            // No output is needed while testing parsing.
        }

        @Override
        public void showTaskUnmarked(Task task, int remainingCount) {
            // No output is needed while testing parsing.
        }

        @Override
        public void showNoTasksOnDate(LocalDate date) {
            lastDateShown = date;
        }
    }

    /**
     * Storage test double that avoids writing files during parser tests.
     */
    private static final class NoOpStorage extends Storage {
        private NoOpStorage() {
            super(Path.of("unused"));
        }

        @Override
        public void saveTasks(List<Task> tasks) {
            // Persistence behavior is covered by StorageTest.
        }
    }
}
