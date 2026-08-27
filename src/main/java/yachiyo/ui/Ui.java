package yachiyo.ui;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

import yachiyo.task.Task;

/**
 * Handles input from and output to the user.
 */
public class Ui implements AutoCloseable {
    private static final String BANNER =
            "__   __    _      ____   _   _   _____  __   __   ___  \n"
            + "\\ \\ / /   / \\    / ___| | | | | |_   _| \\ \\ / /  / _ \\ \n"
            + " \\ V /   / _ \\  | |     | |_| |   | |    \\ V /  | | | |\n"
            + "  | |   / ___ \\ | |___  |  _  |  _| |_    | |   | |_| |\n"
            + "  |_|  /_/   \\_\\ \\____| |_| |_| |_____|   |_|    \\___/ \n";
    private static final String GREETING =
            "Hello! Yachiyo here!\nWhat shall we accomplish today?";
    private static final String EXIT_MESSAGE = "Until we meet again. Take care!~";
    private static final String BREAKER =
            "===================================================================================";
    private static final DateTimeFormatter DATE_DISPLAY_FORMATTER =
            DateTimeFormatter.ofPattern("MMM dd yyyy", Locale.ENGLISH);

    private final Scanner scanner;

    /**
     * Creates a user interface that reads commands from standard input.
     */
    public Ui() {
        this.scanner = new Scanner(System.in);
    }

    /**
     * Checks whether another line of user input is available.
     *
     * @return true if another command can be read.
     */
    public boolean hasNextCommand() {
        return scanner.hasNextLine();
    }

    /**
     * Reads the next command entered by the user.
     *
     * @return the next input line.
     */
    public String readCommand() {
        return scanner.nextLine();
    }

    /**
     * Displays Yachiyo's banner and greeting.
     */
    public void showIntroduction() {
        System.out.println(BANNER);
        System.out.println(GREETING);
        System.out.println(BREAKER);
    }

    /**
     * Displays the divider before a command response.
     */
    public void showCommandStart() {
        System.out.println(BREAKER);
    }

    /**
     * Displays the divider and spacing after a command response.
     */
    public void showCommandEnd() {
        System.out.println(BREAKER);
        System.out.println();
    }

    /**
     * Displays an error message to the user.
     *
     * @param message explanation of the error.
     */
    public void showError(String message) {
        System.out.println(message);
    }

    /**
     * Displays all tasks with their one-based task numbers.
     *
     * @param tasks tasks to display.
     */
    public void showTaskList(List<Task> tasks) {
        if (tasks.isEmpty()) {
            System.out.println("Our lineup is empty for now. What shall we take on next?");
            return;
        }

        System.out.println("Here's everything in our lineup:");
        for (int i = 0; i < tasks.size(); i++) {
            showIndexedTask(i + 1, tasks.get(i));
        }
    }

    /**
     * Displays the heading for tasks whose descriptions match a keyword.
     *
     * @param keyword Keyword matched by the displayed tasks.
     */
    public void showMatchingTasksHeader(String keyword) {
        System.out.printf("Here are the tasks in our lineup matching \"%s\":%n", keyword);
    }

    /**
     * Reports that no task descriptions match a keyword.
     *
     * @param keyword Keyword searched by the user.
     */
    public void showNoMatchingTasks(String keyword) {
        System.out.printf("I couldn't find any tasks matching \"%s\".%n", keyword);
    }

    /**
     * Displays the heading for tasks occurring on a specified date.
     *
     * @param date date whose matching tasks will be displayed.
     */
    public void showTasksOnDateHeader(LocalDate date) {
        System.out.printf("Here are the deadlines and events on %s:%n",
                date.format(DATE_DISPLAY_FORMATTER));
    }

    /**
     * Reports that no deadlines or events occur on a specified date.
     *
     * @param date date checked by the user.
     */
    public void showNoTasksOnDate(LocalDate date) {
        System.out.printf("There are no deadlines or events on %s.%n",
                date.format(DATE_DISPLAY_FORMATTER));
    }

    /**
     * Displays one task with its number from the complete task list.
     *
     * @param taskNumber one-based number of the task.
     * @param task task to display.
     */
    public void showIndexedTask(int taskNumber, Task task) {
        System.out.printf("%d.%s%n", taskNumber, task);
    }

    /**
     * Reports that a task was already completed.
     *
     * @param task completed task.
     */
    public void showAlreadyMarked(Task task) {
        System.out.println("This task is already shining as complete!");
        showTask(task);
    }

    /**
     * Confirms that a task was marked and reports how many remain.
     *
     * @param task task that was marked.
     * @param remainingCount number of incomplete tasks.
     */
    public void showTaskMarked(Task task, int remainingCount) {
        System.out.println("Woohoo! Another task is complete:");
        showTask(task);
        if (remainingCount == 0) {
            System.out.println("Wonderful—everything in our lineup is complete!");
        } else {
            System.out.printf("And with that, our lineup now has %d task%s remaining!%n",
                    remainingCount, remainingCount == 1 ? "" : "s");
        }
    }

    /**
     * Reports that a task was already incomplete.
     *
     * @param task incomplete task.
     */
    public void showAlreadyUnmarked(Task task) {
        System.out.println("No changes needed-this task is already waiting in our lineup!");
        showTask(task);
    }

    /**
     * Confirms that a task was unmarked and reports how many remain.
     *
     * @param task task that was unmarked.
     * @param remainingCount number of incomplete tasks.
     */
    public void showTaskUnmarked(Task task, int remainingCount) {
        System.out.println("Not quite finished? No worries, I've marked it as not done:");
        showTask(task);
        System.out.printf("Our lineup now has %d task%s remaining!%n",
                remainingCount, remainingCount == 1 ? "" : "s");
    }

    /**
     * Confirms that a task was added and reports the new total.
     *
     * @param task task that was added.
     * @param totalCount total number of tasks.
     */
    public void showTaskAdded(Task task, int totalCount) {
        System.out.println("All right, I've added this to our lineup:");
        showTask(task);
        System.out.printf("And with that, our lineup now has %d task%s in total!%n",
                totalCount, totalCount == 1 ? "" : "s");
    }

    /**
     * Confirms that a task was deleted and reports the new total.
     *
     * @param task task that was deleted.
     * @param totalCount total number of remaining tasks.
     */
    public void showTaskDeleted(Task task, int totalCount) {
        System.out.println("All right, I've taken this task out of our lineup:");
        showTask(task);
        if (totalCount == 0) {
            System.out.println("And with that, our lineup is empty again. "
                    + "What shall we take on next?");
        } else {
            System.out.printf("And with that, our lineup now has %d task%s in total!%n",
                    totalCount, totalCount == 1 ? "" : "s");
        }
    }

    /**
     * Displays Yachiyo's farewell and closing divider.
     */
    public void showExit() {
        System.out.println(EXIT_MESSAGE);
        System.out.println(BREAKER);
    }

    private void showTask(Task task) {
        System.out.printf("- %s%n", task);
    }

    /**
     * Releases the input scanner when the application finishes.
     */
    @Override
    public void close() {
        scanner.close();
    }
}
