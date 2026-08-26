package yachiyo;

import java.nio.file.Path;

import yachiyo.command.Command;
import yachiyo.exception.YachiyoException;
import yachiyo.parser.Parser;
import yachiyo.storage.Storage;
import yachiyo.task.TaskList;
import yachiyo.ui.Ui;

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

            boolean isExit = false;
            while (!isExit && ui.hasNextCommand()) {
                String userInput = ui.readCommand().trim();

                // Skip empty inputs
                if (userInput.isEmpty()) {
                    continue;
                }

                ui.showCommandStart();

                try {
                    Command command = Parser.parse(userInput);
                    command.execute(tasks, ui, storage);
                    isExit = command.isExit();
                } catch (YachiyoException e) {
                    ui.showError(e.getMessage());
                } finally {
                    if (!isExit) {
                        ui.showCommandEnd();
                    }
                }
            }
        }
    }
}
