package cloud.eagle.assessment.domain.dto;

import java.time.Instant;

/**
 * Standard error response following problem detail format.
 */
public record ErrorResponse(
    int status,
    String error,
    String message,
    String path,
    Instant timestamp,
    String correlationId
) {
    public static ErrorResponse of(final int status, final String error, final String message, final String path) {
        return new ErrorResponse(status, error, message, path, Instant.now(), generateCorrelationId());
    }

    private static String generateCorrelationId() {
        return java.util.UUID.randomUUID().toString();
    }
}

