package yachiyo.exception;

/**
 * Represents an error that prevents Yachiyo from completing an operation.
 */
public class YachiyoException extends Exception {
    /**
     * Creates an exception with the specified explanation.
     *
     * @param message Explanation of the error.
     */
    public YachiyoException(String message) {
        super(message);
    }
}
