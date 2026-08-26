import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Yachiyo {
    private static final Path DATA_FILE_PATH = Path.of("data", "yachiyo.txt");

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

                try {
                    ParsedCommand command = Parser.parseCommand(userInput);
                    String arguments = command.arguments();

                    switch (command.type()) {
                        case MARK -> markTask(arguments);

                        case UNMARK -> unmarkTask(arguments);

                        case LIST -> listTasks();

                        case TODO -> addTask(Parser.parseToDo(arguments));

                        case DEADLINE -> addTask(Parser.parseDeadline(arguments));

                        case EVENT -> addTask(Parser.parseEvent(arguments));

                        case ON -> listTasksOnDate(Parser.parseDate(arguments));

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

    private int getTaskIndex(int taskNumber) throws YachiyoException {
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

        int index = getTaskIndex(Parser.parseTaskNumber(arguments));
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

        int index = getTaskIndex(Parser.parseTaskNumber(arguments));
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
     * @param date date to check
     */
    private void listTasksOnDate(LocalDate date) {
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

        int index = getTaskIndex(Parser.parseTaskNumber(arguments));
        Task task = tasks.remove(index);
        storage.saveTasks(tasks);
        ui.showTaskDeleted(task, tasks.size());
    }

    private void exit() {
        ui.showExit();
    }

}
