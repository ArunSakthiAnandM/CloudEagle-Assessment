package cloud.eagle.assessment.domain.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * Request to trigger user fetch from a specific source.
 */
public record FetchUsersRequest(
    @NotBlank String sourceName
) {
}

