package cloud.eagle.assessment.controller;

import cloud.eagle.assessment.domain.dto.ErrorResponse;
import cloud.eagle.assessment.exception.ApiConfigurationNotFoundException;
import cloud.eagle.assessment.exception.ApplicationException;
import cloud.eagle.assessment.exception.ExternalApiException;
import cloud.eagle.assessment.exception.FieldMappingException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

/**
 * Global exception handler for REST controllers.
 * Centralizes error handling and returns structured error responses.
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(ApiConfigurationNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleApiConfigurationNotFound(
        final ApiConfigurationNotFoundException ex,
        final HttpServletRequest request
    ) {
        log.error("API configuration not found: {}", ex.getMessage());
        final ErrorResponse error = ErrorResponse.of(
            HttpStatus.NOT_FOUND.value(),
            "Not Found",
            ex.getMessage(),
            request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(ExternalApiException.class)
    public ResponseEntity<ErrorResponse> handleExternalApiException(
        final ExternalApiException ex,
        final HttpServletRequest request
    ) {
        log.error("External API error: {}", ex.getMessage(), ex);
        final ErrorResponse error = ErrorResponse.of(
            HttpStatus.BAD_GATEWAY.value(),
            "Bad Gateway",
            ex.getMessage(),
            request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(error);
    }

    @ExceptionHandler(FieldMappingException.class)
    public ResponseEntity<ErrorResponse> handleFieldMappingException(
        final FieldMappingException ex,
        final HttpServletRequest request
    ) {
        log.error("Field mapping error: {}", ex.getMessage(), ex);
        final ErrorResponse error = ErrorResponse.of(
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "Internal Server Error",
            ex.getMessage(),
            request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
        final MethodArgumentNotValidException ex,
        final HttpServletRequest request
    ) {
        final String message = ex.getBindingResult().getFieldErrors().stream()
            .map(FieldError::getDefaultMessage)
            .collect(Collectors.joining(", "));

        log.error("Validation error: {}", message);
        final ErrorResponse error = ErrorResponse.of(
            HttpStatus.BAD_REQUEST.value(),
            "Bad Request",
            "Validation failed: " + message,
            request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(ApplicationException.class)
    public ResponseEntity<ErrorResponse> handleApplicationException(
        final ApplicationException ex,
        final HttpServletRequest request
    ) {
        log.error("Application error: {}", ex.getMessage(), ex);
        final ErrorResponse error = ErrorResponse.of(
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "Internal Server Error",
            ex.getMessage(),
            request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(
        final Exception ex,
        final HttpServletRequest request
    ) {
        log.error("Unexpected error: {}", ex.getMessage(), ex);
        final ErrorResponse error = ErrorResponse.of(
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "Internal Server Error",
            "An unexpected error occurred",
            request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}

