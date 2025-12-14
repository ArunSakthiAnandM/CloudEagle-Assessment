package cloud.eagle.assessment.domain.dto;

/**
 * Response for user fetch operation.
 */
public record FetchUsersResponse(
    String sourceName,
    int usersFetched,
    String message
) {
}

