package yachiyo;

import java.io.StringWriter;
import java.nio.file.Path;

import yachiyo.command.Command;
import yachiyo.exception.YachiyoException;
import yachiyo.parser.Parser;
import yachiyo.storage.Storage;
import yachiyo.task.TaskList;
import yachiyo.ui.Ui;

/**
 * Runs the Yachiyo task management application.
 */
public class Yachiyo {
    private static final Path DATA_FILE_PATH = Path.of("data", "yachiyo.txt");

    private TaskList tasks = new TaskList();
    private final Storage storage;
    private final Ui ui = new Ui();
    private boolean isInitialized;
    private boolean isExitRequested;

    /**
     * Creates a task manager backed by the default data file.
     */
    public Yachiyo() {
        this(DATA_FILE_PATH);
    }

    /**
     * Creates a task manager backed by the specified data file.
     *
     * @param dataFilePath file used to load and save tasks.
     */
    Yachiyo(Path dataFilePath) {
        this.storage = new Storage(dataFilePath);
    }

    /**
     * Starts the Yachiyo application.
     *
     * @param args Command-line arguments; not used.
     */
    public static void main(String[] args) {
        new Yachiyo().run();
    }

    /**
     * Processes a command and returns the response produced by the task manager.
     *
     * @param input user's message.
     * @return response produced by parsing and executing the command.
     */
    public String getResponse(String input) {
        StringWriter responseWriter = new StringWriter();
        try (Ui responseUi = new Ui(responseWriter)) {
            initializeTasks(responseUi);
            isExitRequested = executeCommand(input.trim(), responseUi);
        }
        return responseWriter.toString().stripTrailing();
    }

    /**
     * Returns whether the last processed command requested an exit.
     *
     * @return true if the last command was {@code bye}.
     */
    public boolean isExitRequested() {
        return isExitRequested;
    }

    /**
     * Returns Yachiyo's introductory greeting.
     *
     * @return greeting shown when the GUI opens.
     */
    public String getGreeting() {
        return Ui.getGreeting();
    }

    /**
     * Runs the command-processing loop until the user exits or input ends.
     */
    private void run() {
        try (Ui ui = this.ui) {
            ui.showIntroduction();
            initializeTasks(ui);

            boolean isExit = false;
            while (!isExit && ui.hasNextCommand()) {
                String userInput = ui.readCommand().trim();

                // Skip empty inputs
                if (userInput.isEmpty()) {
                    continue;
                }

                ui.showCommandStart();

                isExit = executeCommand(userInput, ui);
                ui.showCommandEnd();
            }
        }
    }

    /**
     * Loads saved tasks once before either interface processes its first command.
     *
     * @param outputUi interface that receives a loading error, if one occurs.
     */
    private void initializeTasks(Ui outputUi) {
        if (isInitialized) {
            return;
        }

        try {
            tasks = new TaskList(storage.loadTasks());
        } catch (YachiyoException e) {
            outputUi.showError(e.getMessage());
        } finally {
            isInitialized = true;
        }
    }

    /**
     * Parses and executes one command using the shared task list and storage.
     *
     * @param input command entered by the user.
     * @param outputUi interface that receives command output.
     * @return true if the command requests that the application exit.
     */
    private boolean executeCommand(String input, Ui outputUi) {
        try {
            Command command = Parser.parse(input);
            command.execute(tasks, outputUi, storage);
            return command.isExit();
        } catch (YachiyoException e) {
            outputUi.showError(e.getMessage());
            return false;
        }
    }
}
