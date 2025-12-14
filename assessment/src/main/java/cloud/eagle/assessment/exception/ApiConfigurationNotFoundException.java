package cloud.eagle.assessment.exception;

/**
 * Exception thrown when API configuration is not found.
 */
public class ApiConfigurationNotFoundException extends ApplicationException {

    public ApiConfigurationNotFoundException(final String sourceName) {
        super("API configuration not found for source: " + sourceName);
    }
}

