/**
 * Marks a selected task as incomplete and saves the updated list.
 */
public class UnmarkCommand extends Command {
    private final String arguments;

    /**
     * Creates a command using the task-number arguments supplied by the user.
     *
     * @param arguments text expected to contain a task number
     */
    public UnmarkCommand(String arguments) {
        this.arguments = arguments;
    }

    /**
     * Unmarks the selected task and displays its updated completion state.
     *
     * @param tasks task list containing the selected task
     * @param ui user interface used to display the result
     * @param storage storage used to persist the updated list
     * @throws YachiyoException if no task can be selected or the list cannot be saved
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws YachiyoException {
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

        task.markAsNotDone();
        storage.saveTasks(tasks.getTasks());
        ui.showTaskUnmarked(task, tasks.getRemainingTaskCount());
    }
}
