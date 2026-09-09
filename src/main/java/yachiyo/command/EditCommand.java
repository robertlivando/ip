package yachiyo.command;

import yachiyo.exception.YachiyoException;
import yachiyo.storage.Storage;
import yachiyo.task.Task;
import yachiyo.task.TaskList;
import yachiyo.ui.Ui;

/**
 * Replaces one detail of a selected task and saves the updated task list.
 */
public abstract class EditCommand extends Command {
    private final int taskNumber;

    /**
     * Creates a command that edits the task with the supplied number.
     *
     * @param taskNumber one-based number of the task to edit.
     */
    protected EditCommand(int taskNumber) {
        this.taskNumber = taskNumber;
    }

    /**
     * Creates an edited task without modifying the supplied task.
     *
     * @param task task whose detail should be replaced.
     * @return replacement task containing the edited detail.
     * @throws YachiyoException if the selected detail does not apply to the task.
     */
    protected abstract Task createEditedTask(Task task) throws YachiyoException;

    /**
     * Replaces one detail of the selected task, saves the list, and displays confirmation.
     *
     * @param tasks task list containing the selected task.
     * @param ui user interface used to display confirmation.
     * @param storage storage used to persist the updated list.
     * @throws YachiyoException if the task cannot be selected, edited, or saved.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws YachiyoException {
        if (tasks.isEmpty()) {
            throw new YachiyoException(
                    "There are no tasks to edit just yet. Let's add one first!"
            );
        }

        Task editedTask = createEditedTask(tasks.get(taskNumber));
        tasks.replace(taskNumber, editedTask);
        storage.saveTasks(tasks.getTasks());
        ui.showTaskEdited(editedTask);
    }
}
