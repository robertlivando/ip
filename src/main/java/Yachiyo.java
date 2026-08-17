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

        // Echo
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNextLine()) {
            String command = scanner.nextLine();

            // Skip empty inputs
            if (command.isEmpty()) {
                continue;
            }

            // Exit when user types bye
            if (command.equals("bye")) {
                System.out.println("========================================================");
                System.out.println(exit);
                System.out.println("========================================================\n");
                break;
            } else {
                // Echo
                System.out.println("========================================================");
                System.out.println(command);
                System.out.println("========================================================\n");
            }
        }
        scanner.close();
    }
}
