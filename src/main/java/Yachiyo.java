import java.util.Scanner;

public class Yachiyo {
    private static final int MAX_TASKS = 100;
    private static final String BANNER =
            "__   __    _      ____   _   _   _____  __   __   ___  \n"
            + "\\ \\ / /   / \\    / ___| | | | | |_   _| \\ \\ / /  / _ \\ \n"
            + " \\ V /   / _ \\  | |     | |_| |   | |    \\ V /  | | | |\n"
            + "  | |   / ___ \\ | |___  |  _  |  _| |_    | |   | |_| |\n"
            + "  |_|  /_/   \\_\\ \\____| |_| |_| |_____|   |_|    \\___/ \n";
    private static final String GREETING = "Hello! Yachiyo here!\n" + "What shall we accomplish today?";
    private static final String EXIT_MESSAGE = "Until we meet again. Take care!~";
    private static final String BREAKER = "========================================================";

    private final Task[] tasks = new Task[MAX_TASKS];
    private int taskCount = 0;

    public static void main(String[] args) {
        new Yachiyo().run();
    }

    private void run() {
        printIntroduction();

        try (Scanner scanner = new Scanner(System.in)) {
            while (scanner.hasNextLine()) {
                String userInput = scanner.nextLine().trim();

                // Skip empty inputs
                if (userInput.isEmpty()) {
                    continue;
                }

                System.out.println(BREAKER);

                String[] words = userInput.split("\\s+");
                String command = words[0];

                switch (command) {
                    case "mark":
                        markTask(words);
                        break;

                    case "unmark":
                        unmarkTask(words);
                        break;

                    case "list":
                        listTasks();
                        break;

                    case "todo":
                        addToDoTask(userInput);
                        break;

                    case "bye":
                        exit();
                        return;

                    default:
                        System.out.println("Oh? I don’t recognize that command just yet. Could you try another one?");
                }
                System.out.println(BREAKER);
                System.out.println();
            }
        }
    }

    private void printIntroduction() {
        System.out.println(BANNER);
        System.out.println(GREETING);
        System.out.println(BREAKER);
    }

    private void printInvalidTaskNumberMessage() {
        System.out.printf("Hmm... choose a task number from 1 to %d, okay?\n", taskCount);
    }

    private boolean isValidTaskNumber(int taskNumber) {
        return taskNumber >= 1 && taskNumber <= taskCount;
    }

    private void markTask(String[] words) {
        // No tasks to mark
        if (taskCount == 0) {
            System.out.println(
                    "There are no tasks to mark just yet. Let's add one first!"
            );
            return;
        }

        // Invalid task number
        int taskNumber = parseTaskNumber(words);
        if (!isValidTaskNumber(taskNumber)) {
            printInvalidTaskNumberMessage();
            return;
        }

        // Task is already marked as completed
        Task task = tasks[taskNumber - 1];
        if (task.isCompleted()) {
            System.out.println("This task is already as shining as complete!");
            System.out.printf("- %s\n", task);
            return;
        }

        // Mark as completed
        task.markAsDone();
        System.out.println("Woohoo! Another task is complete:");
        System.out.printf("- %s\n", task);
    }

    private void unmarkTask(String[] words) {
        // No tasks to unmark
        if (taskCount == 0) {
            System.out.println(
                    "There are no tasks to unmark just yet. Let's add one first!"
            );
            return;
        }

        // Invalid task number
        int taskNumber = parseTaskNumber(words);
        if (!isValidTaskNumber(taskNumber)) {
            printInvalidTaskNumberMessage();
            return;
        }

        // Task is already not marked as completed
        Task task = tasks[taskNumber - 1];
        if (!task.isCompleted()) {
            System.out.println("This task is already marked as not done.");
            System.out.printf("- %s\n", task);
            return;
        }

        // Mark as not completed
        task.markAsNotDone();
        System.out.println("Not quite finished? No worries, I've marked it as not done:");
        System.out.printf("- %s\n", task);
    }

    private void listTasks() {
        if (taskCount == 0) {
            System.out.println("Our lineup is empty for now. What shall we take on next?");
            return;
        }

        System.out.println("Here's our lineup for today:");
        for (int i = 0; i < taskCount; i++) {
            System.out.printf("%d.%s\n", i + 1, tasks[i]);
        }
    }

    private void addToDoTask(String userInput) {
        if (taskCount >= tasks.length) {
            System.out.println("Our lineup is completely full! Let's finish something first.");
            return;
        }

        String[] parts = userInput.split("\\s+", 2);
        if (parts.length < 2 || parts[1].isBlank()) {
            System.out.println("It seems this to-do is missing a description. What would you like to accomplish?");
            return;
        }

        String description = parts[1];
        tasks[taskCount] = new ToDo(description);
        taskCount++;
        System.out.println("All right, I've added this to our lineup:");
        System.out.printf("- %s\n", tasks[taskCount - 1]);
        System.out.printf("And with that, our lineup now has %d task%s!%n", taskCount, taskCount == 1 ? "" : "s");
    }

    private void exit() {
        System.out.println(EXIT_MESSAGE);
        System.out.println(BREAKER);
    }

    private int parseTaskNumber(String[] words) {
        if (words.length != 2) {
            return -1;
        }

        try {
            return Integer.parseInt(words[1]);
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}
