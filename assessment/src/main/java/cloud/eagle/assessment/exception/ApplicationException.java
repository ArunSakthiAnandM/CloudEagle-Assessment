package cloud.eagle.assessment.exception;

/**
 * Base exception for all application-specific exceptions.
 */
public class ApplicationException extends RuntimeException {

    public ApplicationException(final String message) {
        super(message);
    }

    public ApplicationException(final String message, final Throwable cause) {
        super(message, cause);
    }
}

