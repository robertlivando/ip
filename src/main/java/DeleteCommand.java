/**
 * Deletes a selected task and saves the updated list.
 */
public class DeleteCommand extends Command {
    private final String arguments;

    /**
     * Creates a command using the task-number arguments supplied by the user.
     *
     * @param arguments text expected to contain a task number
     */
    public DeleteCommand(String arguments) {
        this.arguments = arguments;
    }

    /**
     * Deletes the selected task, persists the updated list, and displays confirmation.
     *
     * @param tasks task list from which to delete
     * @param ui user interface used to display confirmation
     * @param storage storage used to persist the updated list
     * @throws YachiyoException if no task can be selected or the list cannot be saved
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws YachiyoException {
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
