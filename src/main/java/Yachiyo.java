import java.nio.file.Path;

public class Yachiyo {
    private static final Path DATA_FILE_PATH = Path.of("data", "yachiyo.txt");

    private TaskList tasks = new TaskList();
    private final Storage storage = new Storage(DATA_FILE_PATH);
    private final Ui ui = new Ui();

    public static void main(String[] args) {
        new Yachiyo().run();
    }

    private void run() {
        try (Ui ui = this.ui) {
            ui.showIntroduction();
            try {
                tasks = new TaskList(storage.loadTasks());
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

                        case LIST -> new ListCommand().execute(tasks, ui, storage);

                        case TODO -> addTask(Parser.parseToDo(arguments));

                        case DEADLINE -> addTask(Parser.parseDeadline(arguments));

                        case EVENT -> addTask(Parser.parseEvent(arguments));

                        case ON -> new FindOnDateCommand(Parser.parseDate(arguments))
                                .execute(tasks, ui, storage);

                        case DELETE -> deleteTask(arguments);

                        case BYE -> {
                            Command exitCommand = new ExitCommand();
                            exitCommand.execute(tasks, ui, storage);
                            if (exitCommand.isExit()) {
                                return;
                            }
                        }
                    }
                } catch (YachiyoException e) {
                    ui.showError(e.getMessage());
                }

                ui.showCommandEnd();
            }
        }
    }

    private void markTask(String arguments) throws YachiyoException {
        // No tasks to mark
        if (tasks.isEmpty()) {
            throw new YachiyoException(
                    "There are no tasks to mark just yet. Let's add one first!"
            );
        }

        int taskNumber = Parser.parseTaskNumber(arguments);
        Task task = tasks.get(taskNumber);
        if (task.isCompleted()) {
            ui.showAlreadyMarked(task);
            return;
        }

        // Mark as completed
        task.markAsDone();
        storage.saveTasks(tasks.getTasks());
        int remainingCount = tasks.getRemainingTaskCount();
        ui.showTaskMarked(task, remainingCount);
    }

    private void unmarkTask(String arguments) throws YachiyoException {
        // No tasks to unmark
        if (tasks.isEmpty()) {
            throw new YachiyoException(
                    "There are no tasks to unmark just yet. Let's add one first!"
            );
        }

        int taskNumber = Parser.parseTaskNumber(arguments);
        Task task = tasks.get(taskNumber);
        if (!task.isCompleted()) {
            ui.showAlreadyUnmarked(task);
            return;
        }

        // Mark as not completed
        task.markAsNotDone();
        storage.saveTasks(tasks.getTasks());
        int remainingCount = tasks.getRemainingTaskCount();
        ui.showTaskUnmarked(task, remainingCount);
    }

    private void addTask(Task task) throws YachiyoException {
        tasks.add(task);
        storage.saveTasks(tasks.getTasks());
        ui.showTaskAdded(task, tasks.size());
    }

    private void deleteTask(String arguments) throws YachiyoException {
        // No tasks to delete
        if (tasks.isEmpty()) {
            throw new YachiyoException(
                    "There are no tasks to delete just yet. Let's add one first!"
            );
        }

        int taskNumber = Parser.parseTaskNumber(arguments);
        Task task = tasks.delete(taskNumber);
        storage.saveTasks(tasks.getTasks());
        ui.showTaskDeleted(task, tasks.size());
    }

}
