import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Loads and saves Yachiyo's task data on the hard disk.
 */
public class Storage {
    private final Path filePath;

    /**
     * Creates a storage manager that writes to the specified file path.
     *
     * @param filePath path of the file used to store tasks
     */
    public Storage(Path filePath) {
        this.filePath = filePath;
    }

    /**
     * Loads all tasks from the data file. A missing file represents an empty task list.
     *
     * @return tasks reconstructed from the data file
     * @throws YachiyoException if the data file cannot be read or contains invalid task data
     */
    public List<Task> loadTasks() throws YachiyoException {
        if (Files.notExists(filePath)) {
            return new ArrayList<>();
        }

        try {
            List<Task> tasks = new ArrayList<>();
            for (String line : Files.readAllLines(filePath, StandardCharsets.UTF_8)) {
                if (!line.isBlank()) {
                    tasks.add(parseTask(line));
                }
            }
            return tasks;
        } catch (IOException e) {
            throw new YachiyoException("Oh no! I can't seem to load your tasks from the data file.");
        }
    }

    /**
     * Reconstructs one task from its saved file representation.
     *
     * @param line saved task data
     * @return reconstructed task
     * @throws YachiyoException if the saved task data is malformed
     */
    private Task parseTask(String line) throws YachiyoException {
        String[] taskParts = line.split(" \\| ", -1);
        if (taskParts.length < 3) {
            throw invalidDataException();
        }

        Task task = switch (taskParts[0]) {
            case "TODO" -> {
                validateTaskParts(taskParts, 3);
                yield new ToDo(taskParts[2]);
            }
            case "DEADLINE" -> {
                validateTaskParts(taskParts, 4);
                yield new Deadline(taskParts[2], parseDateTime(taskParts[3]));
            }
            case "EVENT" -> {
                validateTaskParts(taskParts, 5);
                yield new Event(taskParts[2], taskParts[3], taskParts[4]);
            }
            default -> throw invalidDataException();
        };

        if (taskParts[1].equals("1")) {
            task.markAsDone();
        } else if (!taskParts[1].equals("0")) {
            throw invalidDataException();
        }
        return task;
    }

    /**
     * Parses a date-time stored in the ISO yyyy-MM-ddTHH:mm format.
     *
     * @param dateTimeText stored date-time text
     * @return parsed date-time
     * @throws YachiyoException if the stored date-time is invalid
     */
    private LocalDateTime parseDateTime(String dateTimeText) throws YachiyoException {
        try {
            return LocalDateTime.parse(dateTimeText);
        } catch (DateTimeParseException e) {
            throw invalidDataException();
        }
    }

    /**
     * Checks that a saved task has the expected number of non-empty fields.
     *
     * @param taskParts saved task fields
     * @param expectedPartCount number of fields required for the task type
     * @throws YachiyoException if a field is missing or empty
     */
    private void validateTaskParts(String[] taskParts, int expectedPartCount) throws YachiyoException {
        if (taskParts.length != expectedPartCount) {
            throw invalidDataException();
        }
        for (String taskPart : taskParts) {
            if (taskPart.isBlank()) {
                throw invalidDataException();
            }
        }
    }

    private YachiyoException invalidDataException() {
        return new YachiyoException("Oh no! Some task data in the file isn't in the expected format.");
    }

    /**
     * Overwrites the data file with the current task list, creating its parent directory if needed.
     *
     * @param tasks current tasks to save
     * @throws YachiyoException if the tasks cannot be written to the file
     */
    public void saveTasks(List<Task> tasks) throws YachiyoException {
        try {
            Path parentDirectory = filePath.getParent();
            if (parentDirectory != null) {
                Files.createDirectories(parentDirectory);
            }

            List<String> taskData = tasks.stream()
                    .map(Task::toFileFormat)
                    .toList();
            Files.write(filePath, taskData, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new YachiyoException("Oh no! I can't seem to save your tasks to the data file.");
        }
    }
}
