package cloud.eagle.assessment.domain.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO for field mapping configuration.
 */
public record FieldMappingDto(
    Long id,
    @NotBlank String internalFieldName,
    @NotBlank String jsonPath,
    String defaultValue,
    boolean required
) {
}

