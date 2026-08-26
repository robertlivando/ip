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
                        case MARK -> new MarkCommand(arguments).execute(tasks, ui, storage);

                        case UNMARK -> new UnmarkCommand(arguments).execute(tasks, ui, storage);

                        case LIST -> new ListCommand().execute(tasks, ui, storage);

                        case TODO -> new AddCommand(Parser.parseToDo(arguments))
                                .execute(tasks, ui, storage);

                        case DEADLINE -> new AddCommand(Parser.parseDeadline(arguments))
                                .execute(tasks, ui, storage);

                        case EVENT -> new AddCommand(Parser.parseEvent(arguments))
                                .execute(tasks, ui, storage);

                        case ON -> new FindOnDateCommand(Parser.parseDate(arguments))
                                .execute(tasks, ui, storage);

                        case DELETE -> new DeleteCommand(arguments).execute(tasks, ui, storage);

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
}
