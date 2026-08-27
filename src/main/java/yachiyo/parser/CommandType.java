package yachiyo.parser;

import yachiyo.exception.YachiyoException;

public enum CommandType {
    MARK,
    UNMARK,
    LIST,
    TODO,
    DEADLINE,
    EVENT,
    ON,
    DELETE,
    BYE;

    public static CommandType parse(String command) throws YachiyoException {
        try {
            return CommandType.valueOf(command.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new YachiyoException(
                    "Oh? I don’t recognize that command just yet. Could you try another one?"
            );
        }
    }
}
