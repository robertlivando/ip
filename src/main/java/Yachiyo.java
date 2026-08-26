import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.ArrayList;
import java.util.List;

public class Yachiyo {
    private static final Path DATA_FILE_PATH = Path.of("data", "yachiyo.txt");
    private static final DateTimeFormatter DATE_TIME_INPUT_FORMATTER = DateTimeFormatter
            .ofPattern("d/M/uuuu HHmm")
            .withResolverStyle(ResolverStyle.STRICT);
    private static final DateTimeFormatter DATE_INPUT_FORMATTER = DateTimeFormatter
            .ofPattern("d/M/uuuu")
            .withResolverStyle(ResolverStyle.STRICT);

    private final List<Task> tasks = new ArrayList<>();
    private final Storage storage = new Storage(DATA_FILE_PATH);
    private final Ui ui = new Ui();

    public static void main(String[] args) {
        new Yachiyo().run();
    }

    private void run() {
        try (Ui ui = this.ui) {
            ui.showIntroduction();
            try {
                tasks.addAll(storage.loadTasks());
            } catch (YachiyoException e) {
                ui.showError(e.getMessage());
            }

            while (ui.hasNextCommand()) {
                String userInput = ui.readCommand().trim();

                // Skip empty inputs
                if (userInput.isEmpty()) {
                    continue;
                }

                ui.showCommandStart();

                String[] inputParts = separateCommand(userInput);
                String arguments = inputParts[1];

                try {
                    CommandType command = CommandType.parse(inputParts[0]);

                    switch (command) {
                        case MARK -> markTask(arguments);

                        case UNMARK -> unmarkTask(arguments);

                        case LIST -> listTasks();

                        case TODO -> addToDoTask(arguments);

                        case DEADLINE -> addDeadlineTask(arguments);

                        case EVENT -> addEventTask(arguments);

                        case ON -> listTasksOnDate(arguments);

                        case DELETE -> deleteTask(arguments);

                        case BYE -> {
                            exit();
                            return;
                        }
                    }
                } catch (YachiyoException e) {
                    ui.showError(e.getMessage());
                }

                ui.showCommandEnd();
            }
        }
    }

    private boolean isValidTaskNumber(int taskNumber) {
        return taskNumber >= 1 && taskNumber <= tasks.size();
    }

    private int getRemainingTaskCount() {
        int remainingCount = 0;
        for (Task task : tasks) {
            if (!task.isCompleted()) {
                remainingCount++;
            }
        }
        return remainingCount;
    }

    private int getTaskIndex(String arguments) throws YachiyoException {
        int taskNumber = parseTaskNumber(arguments);
        if (!isValidTaskNumber(taskNumber)) {
            throw new YachiyoException(
                    String.format("Hmm... choose a task number from 1 to %d, okay?", tasks.size())
            );
        }
        return taskNumber - 1;
    }

    private void markTask(String arguments) throws YachiyoException {
        // No tasks to mark
        if (tasks.isEmpty()) {
            throw new YachiyoException(
                    "There are no tasks to mark just yet. Let's add one first!"
            );
        }

        int index = getTaskIndex(arguments);
        Task task = tasks.get(index);
        if (task.isCompleted()) {
            ui.showAlreadyMarked(task);
            return;
        }

        // Mark as completed
        task.markAsDone();
        storage.saveTasks(tasks);
        int remainingCount = getRemainingTaskCount();
        ui.showTaskMarked(task, remainingCount);
    }

    private void unmarkTask(String arguments) throws YachiyoException {
        // No tasks to unmark
        if (tasks.isEmpty()) {
            throw new YachiyoException(
                    "There are no tasks to unmark just yet. Let's add one first!"
            );
        }

        int index = getTaskIndex(arguments);
        Task task = tasks.get(index);
        if (!task.isCompleted()) {
            ui.showAlreadyUnmarked(task);
            return;
        }

        // Mark as not completed
        task.markAsNotDone();
        storage.saveTasks(tasks);
        int remainingCount = getRemainingTaskCount();
        ui.showTaskUnmarked(task, remainingCount);
    }

    private void listTasks() {
        ui.showTaskList(tasks);
    }

    /**
     * Prints deadlines and events that occur on the date supplied by the user.
     * Matching tasks retain their numbers from the complete task list.
     *
     * @param dateText date supplied in d/M/yyyy format
     * @throws YachiyoException if the date is missing or invalid
     */
    private void listTasksOnDate(String dateText) throws YachiyoException {
        if (dateText.isBlank()) {
            throw new YachiyoException(
                    "Which date should I check? Please enter it as d/M/yyyy."
            );
        }

        LocalDate date;
        try {
            date = LocalDate.parse(dateText, DATE_INPUT_FORMATTER);
        } catch (DateTimeParseException e) {
            throw new YachiyoException(
                    "Hmm, please enter the date as d/M/yyyy, for example 2/12/2026."
            );
        }

        boolean hasMatchingTask = false;
        for (Task task : tasks) {
            if (task.occursOn(date)) {
                hasMatchingTask = true;
                break;
            }
        }

        if (!hasMatchingTask) {
            ui.showNoTasksOnDate(date);
            return;
        }

        ui.showTasksOnDateHeader(date);
        for (int i = 0; i < tasks.size(); i++) {
            Task task = tasks.get(i);
            if (task.occursOn(date)) {
                ui.showIndexedTask(i + 1, task);
            }
        }
    }

    private void addToDoTask(String description) throws YachiyoException {
        if (description.isBlank()) {
            throw new YachiyoException(
                    "Hmm, this to-do is missing a description. What shall we call it?"
            );
        }

        addTask(new ToDo(description));
    }

    private void addDeadlineTask(String taskDetails) throws YachiyoException {
        String[] deadlineParts = taskDetails.split("(?<!\\S)/by(?!\\S)", 2);
        String description = deadlineParts[0].trim();
        if (description.isBlank()) {
            throw new YachiyoException(
                    "Hmm, this deadline is missing a description. What shall we call it?"
            );
        }

        if (deadlineParts.length < 2 || deadlineParts[1].trim().isBlank()) {
            throw new YachiyoException(
                    "It seems this task is missing a deadline. When should it be completed?"
            );
        }
        String dateTimeText = deadlineParts[1].trim();
        LocalDateTime by = parseDateTime(dateTimeText, "deadline");

        addTask(new Deadline(description, by));
    }

    private void addEventTask(String taskDetails) throws YachiyoException {
        String[] eventParts = taskDetails.split("(?<!\\S)/from(?!\\S)", 2);
        String description = eventParts[0].trim();
        if (description.isBlank()) {
            throw new YachiyoException(
                    "Hmm, this event is missing a description. What shall we call it?"
            );
        }

        if (eventParts.length < 2 || eventParts[1].trim().isBlank()) {
            throw new YachiyoException(
                    "This event still needs a start time. When should it begin?"
            );
        }

        String durationDetails = eventParts[1].trim();
        String[] durationParts = durationDetails.split("(?<!\\S)/to(?!\\S)", 2);
        String fromText = durationParts[0].trim();
        if (fromText.isBlank()) {
            throw new YachiyoException(
                    "This event still needs a start time. When should it begin?"
            );
        }

        if (durationParts.length < 2 || durationParts[1].trim().isBlank()) {
            throw new YachiyoException(
                    "And when should this event come to an end?"
            );
        }
        String toText = durationParts[1].trim();

        LocalDateTime from = parseDateTime(fromText, "event start");
        LocalDateTime to = parseDateTime(toText, "event end");
        if (!to.isAfter(from)) {
            throw new YachiyoException("Hmm, the event should end after it starts.");
        }

        addTask(new Event(description, from, to));
    }

    /**
     * Parses a date-time field using the format accepted in user commands.
     *
     * @param dateTimeText date-time text supplied by the user
     * @param fieldName name used to identify the field in error messages
     * @return parsed date-time
     * @throws YachiyoException if the supplied date-time is invalid
     */
    private LocalDateTime parseDateTime(String dateTimeText, String fieldName)
            throws YachiyoException {
        try {
            return LocalDateTime.parse(dateTimeText, DATE_TIME_INPUT_FORMATTER);
        } catch (DateTimeParseException e) {
            throw new YachiyoException(
                    String.format("Hmm, please enter the %s as d/M/yyyy HHmm, "
                            + "for example 2/12/2019 1800.", fieldName)
            );
        }
    }

    private void addTask(Task task) throws YachiyoException {
        tasks.add(task);
        storage.saveTasks(tasks);
        ui.showTaskAdded(task, tasks.size());
    }

    private void deleteTask(String arguments) throws YachiyoException {
        // No tasks to delete
        if (tasks.isEmpty()) {
            throw new YachiyoException(
                    "There are no tasks to delete just yet. Let's add one first!"
            );
        }

        int index = getTaskIndex(arguments);
        Task task = tasks.remove(index);
        storage.saveTasks(tasks);
        ui.showTaskDeleted(task, tasks.size());
    }

    private void exit() {
        ui.showExit();
    }

    private int parseTaskNumber(String arguments) throws YachiyoException {
        if (arguments.isBlank()) {
            throw new YachiyoException(
                    "Which task should I use? Tell me its number!"
            );
        }

        try {
            return Integer.parseInt(arguments);
        } catch (NumberFormatException e) {
            throw new YachiyoException(
                    "Hmm... task numbers need to be whole numbers, okay?"
            );
        }
    }

    private String[] separateCommand(String userInput) {
        String[] parts = userInput.trim().split("\\s+", 2);

        String command = parts[0];
        String arguments = parts.length == 2 ? parts[1].trim() : "";

        return new String[] {command, arguments};
    }
}
