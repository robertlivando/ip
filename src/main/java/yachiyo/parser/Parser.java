package yachiyo.parser;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;

import yachiyo.command.AddCommand;
import yachiyo.command.Command;
import yachiyo.command.DeleteCommand;
import yachiyo.command.ExitCommand;
import yachiyo.command.FindOnDateCommand;
import yachiyo.command.ListCommand;
import yachiyo.command.MarkCommand;
import yachiyo.command.UnmarkCommand;
import yachiyo.exception.YachiyoException;
import yachiyo.task.Deadline;
import yachiyo.task.Event;
import yachiyo.task.ToDo;

/**
 * Interprets user commands and converts their arguments into application objects.
 */
public final class Parser {
    private static final DateTimeFormatter DATE_TIME_INPUT_FORMATTER = DateTimeFormatter
            .ofPattern("d/M/uuuu HHmm")
            .withResolverStyle(ResolverStyle.STRICT);
    private static final DateTimeFormatter DATE_INPUT_FORMATTER = DateTimeFormatter
            .ofPattern("d/M/uuuu")
            .withResolverStyle(ResolverStyle.STRICT);

    private Parser() {
    }

    /**
     * Interprets a user input line and creates the command that should handle it.
     *
     * @param userInput complete input entered by the user
     * @return executable command represented by the input
     * @throws YachiyoException if the command or its arguments are invalid
     */
    public static Command parse(String userInput) throws YachiyoException {
        String[] parts = userInput.trim().split("\\s+", 2);
        CommandType type = CommandType.parse(parts[0]);
        String arguments = parts.length == 2 ? parts[1].trim() : "";

        return switch (type) {
            case MARK -> new MarkCommand(parseTaskNumber(arguments));
            case UNMARK -> new UnmarkCommand(parseTaskNumber(arguments));
            case LIST -> new ListCommand();
            case TODO -> new AddCommand(parseToDo(arguments));
            case DEADLINE -> new AddCommand(parseDeadline(arguments));
            case EVENT -> new AddCommand(parseEvent(arguments));
            case ON -> new FindOnDateCommand(parseDate(arguments));
            case DELETE -> new DeleteCommand(parseTaskNumber(arguments));
            case BYE -> new ExitCommand();
        };
    }

    /**
     * Parses a task number supplied to a task-related command.
     *
     * @param arguments text expected to contain a task number
     * @return parsed one-based task number
     * @throws YachiyoException if the task number is missing or is not a whole number
     */
    private static int parseTaskNumber(String arguments) throws YachiyoException {
        if (arguments.isBlank()) {
            throw new YachiyoException(
                    "Which task should I use? Tell me its number!"
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

    /**
     * Parses a date supplied to the {@code on} command.
     *
     * @param dateText date supplied in d/M/yyyy format
     * @return parsed date
     * @throws YachiyoException if the date is missing or invalid
     */
    private static LocalDate parseDate(String dateText) throws YachiyoException {
        if (dateText.isBlank()) {
            throw new YachiyoException(
                    "Which date should I check? Please enter it as d/M/yyyy."
            );
        }

        try {
            return LocalDate.parse(dateText, DATE_INPUT_FORMATTER);
        } catch (DateTimeParseException e) {
            throw new YachiyoException(
                    "Hmm, please enter the date as d/M/yyyy, for example 2/12/2026."
            );
        }
    }

    /**
     * Creates a to-do task from command arguments.
     *
     * @param description task description supplied by the user
     * @return parsed to-do task
     * @throws YachiyoException if the description is missing
     */
    private static ToDo parseToDo(String description) throws YachiyoException {
        if (description.isBlank()) {
            throw new YachiyoException(
                    "Hmm, this to-do is missing a description. What shall we call it?"
            );
        }
        return new ToDo(description);
    }

    /**
     * Creates a deadline task from command arguments.
     *
     * @param taskDetails description and deadline supplied by the user
     * @return parsed deadline task
     * @throws YachiyoException if a required field is missing or invalid
     */
    private static Deadline parseDeadline(String taskDetails) throws YachiyoException {
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

        LocalDateTime by = parseDateTime(deadlineParts[1].trim(), "deadline");
        return new Deadline(description, by);
    }

    /**
     * Creates an event task from command arguments.
     *
     * @param taskDetails description, start, and end supplied by the user
     * @return parsed event task
     * @throws YachiyoException if a required field is missing or invalid
     */
    private static Event parseEvent(String taskDetails) throws YachiyoException {
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

        String[] durationParts = eventParts[1].trim().split("(?<!\\S)/to(?!\\S)", 2);
        String fromText = durationParts[0].trim();
        if (fromText.isBlank()) {
            throw new YachiyoException(
                    "This event still needs a start time. When should it begin?"
            );
        }

        if (durationParts.length < 2 || durationParts[1].trim().isBlank()) {
            throw new YachiyoException(
                    "And when should this event come to an end?"
            );
        }

        LocalDateTime from = parseDateTime(fromText, "event start");
        LocalDateTime to = parseDateTime(durationParts[1].trim(), "event end");
        if (!to.isAfter(from)) {
            throw new YachiyoException("Hmm, the event should end after it starts.");
        }

        return new Event(description, from, to);
    }

    /**
     * Parses a date-time field using the format accepted in user commands.
     *
     * @param dateTimeText date-time text supplied by the user
     * @param fieldName name used to identify the field in error messages
     * @return parsed date-time
     * @throws YachiyoException if the supplied date-time is invalid
     */
    private static LocalDateTime parseDateTime(String dateTimeText, String fieldName)
            throws YachiyoException {
        try {
            return LocalDateTime.parse(dateTimeText, DATE_TIME_INPUT_FORMATTER);
        } catch (DateTimeParseException e) {
            throw new YachiyoException(
                    String.format("Hmm, please enter the %s as d/M/yyyy HHmm, "
                            + "for example 2/12/2019 1800.", fieldName)
            );
        }
    }
}
