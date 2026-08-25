import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Saves Yachiyo's task data to the hard disk.
 */
public class Storage {
    private final Path filePath;

    /**
     * Creates a storage manager that writes to the specified file path.
     *
     * @param filePath path of the file used to store tasks
     */
    public Storage(String filePath) {
        this.filePath = Path.of(filePath);
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
