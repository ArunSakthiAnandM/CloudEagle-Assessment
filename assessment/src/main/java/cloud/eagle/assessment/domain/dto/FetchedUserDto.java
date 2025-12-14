package cloud.eagle.assessment.domain.dto;

import jakarta.validation.constraints.NotBlank;

import java.time.Instant;

/**
 * DTO for fetched user data exposed through the API.
 */
public record FetchedUserDto(
    Long id,
    @NotBlank String sourceName,
    @NotBlank String externalId,
    String email,
    String name,
    String firstName,
    String lastName,
    String timezone,
    String avatarUrl,
    Instant fetchedAt
) {
}

