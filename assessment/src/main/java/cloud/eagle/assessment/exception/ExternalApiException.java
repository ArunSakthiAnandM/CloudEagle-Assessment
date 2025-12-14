package cloud.eagle.assessment.exception;

/**
 * Exception thrown when external API call fails.
 */
public class ExternalApiException extends ApplicationException {

    public ExternalApiException(final String message) {
        super(message);
    }

    public ExternalApiException(final String message, final Throwable cause) {
        super(message, cause);
    }
}

