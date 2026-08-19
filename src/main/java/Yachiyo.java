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
    private int completedCount = 0;

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

                String[] inputParts = separateCommand(userInput);
                String command = inputParts[0];
                String arguments = inputParts[1];

                switch (command) {
                    case "mark":
                        markTask(arguments);
                        break;

                    case "unmark":
                        unmarkTask(arguments);
                        break;

                    case "list":
                        listTasks();
                        break;

                    case "todo":
                        addToDoTask(arguments);
                        break;

                    case "deadline":
                        addDeadlineTask(arguments);
                        break;

                    case "event":
                        addEventTask(arguments);
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

    private void printTaskIsFullMessage() {
        System.out.println("Our lineup is completely full! Let's finish something first.");
    }

    private boolean isValidTaskNumber(int taskNumber) {
        return taskNumber >= 1 && taskNumber <= taskCount;
    }

    private void markTask(String arguments) {
        // No tasks to mark
        if (taskCount == 0) {
            System.out.println(
                    "There are no tasks to mark just yet. Let's add one first!"
            );
            return;
        }

        // Invalid task number
        int taskNumber = parseTaskNumber(arguments);
        if (!isValidTaskNumber(taskNumber)) {
            printInvalidTaskNumberMessage();
            return;
        }

        // Task is already marked as completed
        Task task = tasks[taskNumber - 1];
        if (task.isCompleted()) {
            System.out.println("This task is already shining as complete!");
            System.out.printf("- %s\n", task);
            return;
        }

        // Mark as completed
        task.markAsDone();
        completedCount++;
        int remainingCount = taskCount - completedCount;
        System.out.println("Woohoo! Another task is complete:");
        System.out.printf("- %s\n", task);
        System.out.printf("And with that, our lineup now has %d task%s remaining!%n",
                remainingCount, remainingCount == 1 ? "" : "s");
    }

    private void unmarkTask(String arguments) {
        // No tasks to unmark
        if (taskCount == 0) {
            System.out.println(
                    "There are no tasks to unmark just yet. Let's add one first!"
            );
            return;
        }

        // Invalid task number
        int taskNumber = parseTaskNumber(arguments);
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
        completedCount--;
        int remainingCount = taskCount - completedCount;
        System.out.println("Not quite finished? No worries, I've marked it as not done:");
        System.out.printf("- %s\n", task);
        System.out.printf("Our lineup now has %d task%s remaining!%n",
                remainingCount, remainingCount == 1 ? "" : "s");
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

    private void addToDoTask(String description) {
        if (description.isBlank()) {
            System.out.println("Hmm, this to-do is missing a description. What shall we call it?");
            return;
        }

        if (taskCount >= tasks.length) {
            printTaskIsFullMessage();
            return;
        }

        tasks[taskCount] = new ToDo(description);
        taskCount++;
        int remainingCount = taskCount - completedCount;
        System.out.println("All right, I've added this to our lineup:");
        System.out.printf("- %s\n", tasks[taskCount - 1]);
        System.out.printf("And with that, our lineup now has %d task%s remaining!%n",
                remainingCount, remainingCount == 1 ? "" : "s");
    }

    private void addDeadlineTask(String taskDetails) {
        String[] deadlineParts = taskDetails.split("(?<!\\S)/by(?!\\S)", 2);
        String description = deadlineParts[0].trim();
        if (description.isBlank()) {
            System.out.println("Hmm, this deadline is missing a description. What shall we call it?");
            return;
        }

        if (deadlineParts.length < 2 || deadlineParts[1].trim().isBlank()) {
            System.out.println("It seems this task is missing a deadline. When should it be completed?");
            return;
        }
        String by = deadlineParts[1].trim();

        if (taskCount >= tasks.length) {
            printTaskIsFullMessage();
            return;
        }

        tasks[taskCount] = new Deadline(description, by);
        taskCount++;
        int remainingCount = taskCount - completedCount;
        System.out.println("All right, I've added this to our lineup:");
        System.out.printf("- %s\n", tasks[taskCount - 1]);
        System.out.printf("And with that, our lineup now has %d task%s remaining!%n",
                remainingCount, remainingCount == 1 ? "" : "s");
    }

    private void addEventTask(String taskDetails) {
        String[] eventParts = taskDetails.split("(?<!\\S)/from(?!\\S)", 2);
        String description = eventParts[0].trim();
        if (description.isBlank()) {
            System.out.println("Hmm, this event is missing a description. What shall we call it?");
            return;
        }

        if (eventParts.length < 2 || eventParts[1].trim().isBlank()) {
            System.out.println("This event still needs a start time. When should it begin?");
            return;
        }

        String durationDetails = eventParts[1].trim();
        String[] durationParts = durationDetails.split("(?<!\\S)/to(?!\\S)", 2);
        String from = durationParts[0].trim();
        if (from.isBlank()) {
            System.out.println("This event still needs a start time. When should it begin?");
            return;
        }

        if (durationParts.length < 2 || durationParts[1].trim().isBlank()) {
            System.out.println("And when should this event come to an end?");
            return;
        }
        String to = durationParts[1].trim();

        if (taskCount >= tasks.length) {
            printTaskIsFullMessage();
            return;
        }

        tasks[taskCount] = new Event(description, from, to);
        taskCount++;
        int remainingCount = taskCount - completedCount;
        System.out.println("All right, I've added this to our lineup:");
        System.out.printf("- %s\n", tasks[taskCount - 1]);
        System.out.printf("And with that, our lineup now has %d task%s remaining!%n",
                remainingCount, remainingCount == 1 ? "" : "s");
    }

    private void exit() {
        System.out.println(EXIT_MESSAGE);
        System.out.println(BREAKER);
    }

    private int parseTaskNumber(String arguments) {
        try {
            return Integer.parseInt(arguments);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private String[] separateCommand(String userInput) {
        String[] parts = userInput.trim().split("\\s+", 2);

        String command = parts[0];
        String arguments = parts.length == 2 ? parts[1].trim() : "";

        return new String[] {command, arguments};
    }
}
