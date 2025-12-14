package cloud.eagle.assessment.domain.dto;

import cloud.eagle.assessment.domain.entity.AuthType;
import cloud.eagle.assessment.domain.entity.HttpMethod;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.util.List;

/**
 * DTO for API configuration exposed through the API.
 */
public record ApiConfigurationDto(
    Long id,
    @NotBlank String sourceName,
    @NotBlank String endpointUrl,
    @NotNull HttpMethod httpMethod,
    @NotNull AuthType authType,
    String requestHeaders,
    String responseRootPath,
    boolean active,
    List<FieldMappingDto> fieldMappings,
    Instant createdAt,
    Instant updatedAt
) {
}

