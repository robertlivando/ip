import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Yachiyo {
    private static final String BANNER =
            "__   __    _      ____   _   _   _____  __   __   ___  \n"
            + "\\ \\ / /   / \\    / ___| | | | | |_   _| \\ \\ / /  / _ \\ \n"
            + " \\ V /   / _ \\  | |     | |_| |   | |    \\ V /  | | | |\n"
            + "  | |   / ___ \\ | |___  |  _  |  _| |_    | |   | |_| |\n"
            + "  |_|  /_/   \\_\\ \\____| |_| |_| |_____|   |_|    \\___/ \n";
    private static final String GREETING = "Hello! Yachiyo here!\n" + "What shall we accomplish today?";
    private static final String EXIT_MESSAGE = "Until we meet again. Take care!~";
    private static final String BREAKER =
            "===================================================================================";

    private final List<Task> tasks = new ArrayList<>();

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

                try {
                    switch (command) {
                        case "mark" -> markTask(arguments);

                        case "unmark" -> unmarkTask(arguments);

                        case "list" -> listTasks();

                        case "todo" -> addToDoTask(arguments);

                        case "deadline" -> addDeadlineTask(arguments);

                        case "event" -> addEventTask(arguments);

                        case "delete" -> deleteTask(arguments);

                        case "bye" -> {
                            exit();
                            return;
                        }

                        default -> throw new YachiyoException(
                                "Oh? I don’t recognize that command just yet. Could you try another one?"
                        );
                    }
                } catch (YachiyoException e) {
                    System.out.println(e.getMessage());
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

    private boolean isValidTaskNumber(int taskNumber) {
        return taskNumber >= 1 && taskNumber <= tasks.size();
    }

    private int getRemainingTaskCount() {
        int remainingCount = 0;
        for (Task task : tasks) {
            if (!task.isCompleted()) {
                remainingCount++;
            }
        }
        return remainingCount;
    }

    private int getTaskIndex(String arguments) throws YachiyoException {
        int taskNumber = parseTaskNumber(arguments);
        if (!isValidTaskNumber(taskNumber)) {
            throw new YachiyoException(
                    String.format("Hmm... choose a task number from 1 to %d, okay?", tasks.size())
            );
        }
        return taskNumber - 1;
    }

    private void markTask(String arguments) throws YachiyoException {
        // No tasks to mark
        if (tasks.isEmpty()) {
            throw new YachiyoException(
                    "There are no tasks to mark just yet. Let's add one first!"
            );
        }

        int index = getTaskIndex(arguments);
        Task task = tasks.get(index);
        if (task.isCompleted()) {
            System.out.println("This task is already shining as complete!");
            System.out.printf("- %s\n", task);
            return;
        }

        // Mark as completed
        task.markAsDone();
        int remainingCount = getRemainingTaskCount();
        System.out.println("Woohoo! Another task is complete:");
        System.out.printf("- %s\n", task);
        if (remainingCount == 0) {
            System.out.println("Wonderful—everything in our lineup is complete!");
        } else {
            System.out.printf("And with that, our lineup now has %d task%s remaining!%n",
                    remainingCount, remainingCount == 1 ? "" : "s");
        }
    }

    private void unmarkTask(String arguments) throws YachiyoException {
        // No tasks to unmark
        if (tasks.isEmpty()) {
            throw new YachiyoException(
                    "There are no tasks to unmark just yet. Let's add one first!"
            );
        }

        int index = getTaskIndex(arguments);
        Task task = tasks.get(index);
        if (!task.isCompleted()) {
            System.out.println("No changes needed-this task is already waiting in our lineup!");
            System.out.printf("- %s\n", task);
            return;
        }

        // Mark as not completed
        task.markAsNotDone();
        int remainingCount = getRemainingTaskCount();
        System.out.println("Not quite finished? No worries, I've marked it as not done:");
        System.out.printf("- %s\n", task);
        System.out.printf("Our lineup now has %d task%s remaining!%n",
                remainingCount, remainingCount == 1 ? "" : "s");
    }

    private void listTasks() {
        if (tasks.isEmpty()) {
            System.out.println("Our lineup is empty for now. What shall we take on next?");
            return;
        }

        System.out.println("Here's everything in our lineup:");
        for (int i = 0; i < tasks.size(); i++) {
            System.out.printf("%d.%s\n", i + 1, tasks.get(i));
        }
    }

    private void addToDoTask(String description) throws YachiyoException {
        if (description.isBlank()) {
            throw new YachiyoException(
                    "Hmm, this to-do is missing a description. What shall we call it?"
            );
        }

        addTask(new ToDo(description));
    }

    private void addDeadlineTask(String taskDetails) throws YachiyoException {
        String[] deadlineParts = taskDetails.split("(?<!\\S)/by(?!\\S)", 2);
        String description = deadlineParts[0].trim();
        if (description.isBlank()) {
            throw new YachiyoException(
                    "Hmm, this deadline is missing a description. What shall we call it?"
            );
        }

        if (deadlineParts.length < 2 || deadlineParts[1].trim().isBlank()) {
            throw new YachiyoException(
                    "It seems this task is missing a deadline. When should it be completed?"
            );
        }
        String by = deadlineParts[1].trim();

        addTask(new Deadline(description, by));
    }

    private void addEventTask(String taskDetails) throws YachiyoException {
        String[] eventParts = taskDetails.split("(?<!\\S)/from(?!\\S)", 2);
        String description = eventParts[0].trim();
        if (description.isBlank()) {
            throw new YachiyoException(
                    "Hmm, this event is missing a description. What shall we call it?"
            );
        }

        if (eventParts.length < 2 || eventParts[1].trim().isBlank()) {
            throw new YachiyoException(
                    "This event still needs a start time. When should it begin?"
            );
        }

        String durationDetails = eventParts[1].trim();
        String[] durationParts = durationDetails.split("(?<!\\S)/to(?!\\S)", 2);
        String from = durationParts[0].trim();
        if (from.isBlank()) {
            throw new YachiyoException(
                    "This event still needs a start time. When should it begin?"
            );
        }

        if (durationParts.length < 2 || durationParts[1].trim().isBlank()) {
            throw new YachiyoException(
                    "And when should this event come to an end?"
            );
        }
        String to = durationParts[1].trim();

        addTask(new Event(description, from, to));
    }

    private void addTask(Task task) {
        tasks.add(task);
        System.out.println("All right, I've added this to our lineup:");
        System.out.printf("- %s\n", task);
        System.out.printf("And with that, our lineup now has %d task%s in total!%n",
                tasks.size(), tasks.size() == 1 ? "" : "s");
    }

    private void deleteTask(String arguments) throws YachiyoException {
        // No tasks to delete
        if (tasks.isEmpty()) {
            throw new YachiyoException(
                    "There are no tasks to delete just yet. Let's add one first!"
            );
        }

        int index = getTaskIndex(arguments);
        Task task = tasks.remove(index);
        System.out.println("All right, I've taken this task out of our lineup:");
        System.out.printf("- %s\n", task);
        if (tasks.isEmpty()) {
            System.out.println("And with that, our lineup is empty again. What shall we take on next?");
        } else {
            System.out.printf("And with that, our lineup now has %d task%s in total!%n",
                    tasks.size(), tasks.size() == 1 ? "" : "s");
        }
    }

    private void exit() {
        System.out.println(EXIT_MESSAGE);
        System.out.println(BREAKER);
    }

    private int parseTaskNumber(String arguments) throws YachiyoException {
        if (arguments.isBlank()) {
            throw new YachiyoException(
                    "Which task should I mark? Tell me its number!"
            );
        }

        try {
            return Integer.parseInt(arguments);
        } catch (NumberFormatException e) {
            throw new YachiyoException(
                    "Hmm... task numbers need to be whole numbers, okay?"
            );
        }
    }

    private String[] separateCommand(String userInput) {
        String[] parts = userInput.trim().split("\\s+", 2);

        String command = parts[0];
        String arguments = parts.length == 2 ? parts[1].trim() : "";

        return new String[] {command, arguments};
    }
}
