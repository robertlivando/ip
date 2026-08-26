/**
 * Contains a recognized command type and the arguments supplied with it.
 *
 * @param type type of command entered by the user
 * @param arguments text following the command word
 */
public record ParsedCommand(CommandType type, String arguments) {
}
