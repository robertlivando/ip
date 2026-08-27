/**
 * Marks a selected task as completed and saves the updated list.
 */
public class MarkCommand extends Command {
    private final String arguments;

    /**
     * Creates a command using the task-number arguments supplied by the user.
     *
     * @param arguments text expected to contain a task number
     */
    public MarkCommand(String arguments) {
        this.arguments = arguments;
    }

    /**
     * Marks the selected task and displays its updated completion state.
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
                    "There are no tasks to mark just yet. Let's add one first!"
            );
        }

        int taskNumber = Parser.parseTaskNumber(arguments);
        Task task = tasks.get(taskNumber);
        if (task.isCompleted()) {
            ui.showAlreadyMarked(task);
            return;
        }

        task.markAsDone();
        storage.saveTasks(tasks.getTasks());
        ui.showTaskMarked(task, tasks.getRemainingTaskCount());
    }
}
