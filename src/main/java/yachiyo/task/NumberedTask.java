package yachiyo.task;

/**
 * Associates a task with its one-based number in the complete task list.
 *
 * @param number one-based task number.
 * @param task numbered task.
 */
public record NumberedTask(int number, Task task) {
}
