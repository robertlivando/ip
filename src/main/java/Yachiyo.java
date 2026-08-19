import java.util.Scanner;

public class Yachiyo {
    public static void main(String[] args) {
        String banner = "__   __    _      ____   _   _   _____  __   __   ___  \n"
                        + "\\ \\ / /   / \\    / ___| | | | | |_   _| \\ \\ / /  / _ \\ \n"
                        + " \\ V /   / _ \\  | |     | |_| |   | |    \\ V /  | | | |\n"
                        + "  | |   / ___ \\ | |___  |  _  |  _| |_    | |   | |_| |\n"
                        + "  |_|  /_/   \\_\\ \\____| |_| |_| |_____|   |_|    \\___/ \n";
        String greeting = "Hello! Yachiyo here!\n" + "What shall we accomplish today?";
        String exit = "Until we meet again. Take care!~";
        String breaker = "========================================================";

        // Introduction (banner + greeting)
        System.out.println(banner);
        System.out.println(greeting);
        System.out.println(breaker);

        // Storing tasks
        String[] tasks = new String[100];
        boolean[] isCompleted = new boolean[100];
        int added = 0;

        // Logic
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNextLine()) {
            String userInput = scanner.nextLine().trim();

            // Skip empty inputs
            if (userInput.isEmpty()) {
                continue;
            }

            String[] words = userInput.split("\\s+");
            String command = words[0];

            System.out.println(breaker);
            switch (command) {
                case "mark":
                    if (added == 0) {
                        System.out.println(
                                "There are no tasks to mark just yet. Let's add one first!"
                        );
                        break;
                    }

                    int taskNumber = 0;
                    boolean validNumber = false;

                    if (words.length == 2) {
                        try {
                            taskNumber = Integer.parseInt(words[1]);
                            validNumber = true;
                        } catch (NumberFormatException e) {
                            validNumber = false;
                        }
                    }

                    if (!validNumber || taskNumber < 1 || taskNumber > added) {
                        System.out.printf("Hmm... choose a task number from 1 to %d, okay?\n", added);
                        break;
                    }

                    int index = taskNumber - 1;
                    if (isCompleted[index]) {
                        System.out.println("This task is already shining as complete!");
                        System.out.printf("- [X] %s\n", tasks[index]);
                        break;
                    }

                    // Mark as completed
                    isCompleted[index] = true;
                    System.out.println("Wonderful! Another task is complete:");
                    System.out.printf("- [X] %s\n", tasks[index]);
                    break;

                case "unmark":
                    if (added == 0) {
                        System.out.println(
                                "There are no tasks to unmark just yet. Let's add one first!"
                        );
                        break;
                    }

                    taskNumber = 0;
                    validNumber = false;

                    if (words.length == 2) {
                        try {
                            taskNumber = Integer.parseInt(words[1]);
                            validNumber = true;
                        } catch (NumberFormatException e) {
                            validNumber = false;
                        }
                    }

                    if (!validNumber || taskNumber < 1 || taskNumber > added) {
                        System.out.printf("Hmm... choose a task number from 1 to %d, okay?\n", added);
                        break;
                    }

                    index = taskNumber - 1;
                    if (!isCompleted[index]) {
                        System.out.println("This task is already marked as not done.");
                        System.out.printf("- [ ] %s\n", tasks[index]);
                        break;
                    }

                    // Mark as not completed
                    isCompleted[index] = false;
                    System.out.println("Not quite finished? No worries, I've marked it as not done:");
                    System.out.printf("- [ ] %s\n", tasks[index]);
                    break;


                case "list":
                    if (added == 0) {
                        System.out.println("Our lineup is empty for now. What shall we take on next?");
                        break;
                    }

                    System.out.println("Here's our lineup for today:");
                    for (int i = 0; i < added; i++) {
                        String mark = isCompleted[i] ? "X" : " ";
                        System.out.printf("%d.[%s] %s\n", i + 1, mark, tasks[i]);
                    }
                    break;

                case "bye":
                    System.out.println(exit);
                    scanner.close();
                    System.out.println(breaker);
                    return;

                default:
                    if (added >= tasks.length) {
                        System.out.println("Our lineup is completely full! Let's finish a few tasks first.");
                        break;
                    }
                    tasks[added] = userInput;
                    isCompleted[added] = false;
                    added++;
                    System.out.println("added: " + userInput);
                    break;
            }
            System.out.println(breaker + "\n");
        }
    }
}
