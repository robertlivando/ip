import java.util.Scanner;

public class Yachiyo {
    public static void main(String[] args) {
        String banner = "__   __    _      ____   _   _   _____  __   __   ___  \n"
                        + "\\ \\ / /   / \\    / ___| | | | | |_   _| \\ \\ / /  / _ \\ \n"
                        + " \\ V /   / _ \\  | |     | |_| |   | |    \\ V /  | | | |\n"
                        + "  | |   / ___ \\ | |___  |  _  |  _| |_    | |   | |_| |\n"
                        + "  |_|  /_/   \\_\\ \\____| |_| |_| |_____|   |_|    \\___/ \n";
        String greeting = "Ohayo! Yachiyo here!\n" + "What shall we create today?";
        String exit = "Until we meet again—have a lovely day!";

        // Introduction (banner + greeting)
        System.out.println(banner);
        System.out.println(greeting);
        System.out.println("========================================================\n");

        // Storing tasks
        String[] tasks = new String[100];
        int added = 0;

        // Echo
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNextLine()) {
            String userInput = scanner.nextLine();

            // Skip empty inputs
            if (userInput.isEmpty()) {
                continue;
            }

            switch (userInput) {
                case "list":
                    System.out.println("========================================================");
                    for (int i = 0; i < added; i++) {
                        System.out.printf("%d. %s\n", i + 1, tasks[i]);
                    }
                    System.out.println("========================================================\n");
                    break;

                case "bye":
                    System.out.println("========================================================");
                    System.out.println(exit);
                    System.out.println("========================================================\n");
                    scanner.close();
                    return;

                default:
                    tasks[added] = userInput;
                    added++;
                    System.out.println("========================================================");
                    System.out.println("added: " + userInput);
                    System.out.println("========================================================\n");
                    break;
            }
        }
    }
}
