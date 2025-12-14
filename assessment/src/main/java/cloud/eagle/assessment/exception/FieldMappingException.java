package cloud.eagle.assessment.exception;

/**
 * Exception thrown when field mapping fails.
 */
public class FieldMappingException extends ApplicationException {

    public FieldMappingException(final String message) {
        super(message);
    }

    public FieldMappingException(final String message, final Throwable cause) {
        super(message, cause);
    }
}

