package cloud.eagle.assessment.service;

import cloud.eagle.assessment.domain.dto.FetchedUserDto;
import cloud.eagle.assessment.domain.dto.FetchUsersResponse;
import cloud.eagle.assessment.domain.entity.ApiConfiguration;
import cloud.eagle.assessment.domain.entity.FetchedUser;
import cloud.eagle.assessment.mapper.EntityMapper;
import cloud.eagle.assessment.repository.FetchedUserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * Service for fetching and storing users from external systems.
 * Orchestrates the integration process using the generic API service.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
public class UserFetchService {

    private final ApiConfigService apiConfigService;
    private final ExternalApiService externalApiService;
    private final FetchedUserRepository fetchedUserRepository;
    private final ObjectMapper objectMapper;

    public UserFetchService(
        final ApiConfigService apiConfigService,
        final ExternalApiService externalApiService,
        final FetchedUserRepository fetchedUserRepository,
        final ObjectMapper objectMapper
    ) {
        this.apiConfigService = apiConfigService;
        this.externalApiService = externalApiService;
        this.fetchedUserRepository = fetchedUserRepository;
        this.objectMapper = objectMapper;
    }

    /**
     * Fetch users from a specific external source.
     *
     * @param sourceName the source name (e.g., "calendly", "dropbox")
     * @return response with fetch statistics
     */
    @Transactional
    public FetchUsersResponse fetchUsersFromSource(final String sourceName) {
        log.info("Starting user fetch from source: {}", sourceName);

        // Get API configuration
        final ApiConfiguration config = apiConfigService.findActiveConfiguration(sourceName);

        // Call external API
        final String jsonResponse = externalApiService.callExternalApi(config);

        // Parse and map response
        final List<Map<String, Object>> mappedUsers = externalApiService.parseAndMapResponse(jsonResponse, config);

        // Store users
        int savedCount = 0;
        for (final Map<String, Object> userData : mappedUsers) {
            if (saveOrUpdateUser(sourceName, userData)) {
                savedCount++;
            }
        }

        log.info("Completed user fetch from source: {}, fetched={}, saved={}",
            sourceName, mappedUsers.size(), savedCount);

        return new FetchUsersResponse(
            sourceName,
            savedCount,
            "Successfully fetched " + savedCount + " users from " + sourceName
        );
    }

    /**
     * Get all fetched users with pagination.
     *
     * @param pageable pagination information
     * @return page of user DTOs
     */
    public Page<FetchedUserDto> getAllUsers(final Pageable pageable) {
        log.debug("Retrieving all fetched users, page: {}", pageable.getPageNumber());
        return fetchedUserRepository.findAll(pageable)
            .map(EntityMapper::toDto);
    }

    /**
     * Get fetched users by source name with pagination.
     *
     * @param sourceName the source name
     * @param pageable pagination information
     * @return page of user DTOs
     */
    public Page<FetchedUserDto> getUsersBySource(final String sourceName, final Pageable pageable) {
        log.debug("Retrieving users from source: {}, page: {}", sourceName, pageable.getPageNumber());
        return fetchedUserRepository.findBySourceName(sourceName, pageable)
            .map(EntityMapper::toDto);
    }

    private boolean saveOrUpdateUser(final String sourceName, final Map<String, Object> userData) {
        try {
            final String externalId = extractRequiredField(userData, "externalId");

            // Check if user already exists
            final FetchedUser user = fetchedUserRepository
                .findBySourceNameAndExternalId(sourceName, externalId)
                .orElseGet(() -> new FetchedUser(sourceName, externalId));

            // Map fields
            user.setEmail(extractField(userData, "email"));
            user.setName(extractField(userData, "name"));
            user.setFirstName(extractField(userData, "firstName"));
            user.setLastName(extractField(userData, "lastName"));
            user.setTimezone(extractField(userData, "timezone"));
            user.setAvatarUrl(extractField(userData, "avatarUrl"));

            // Store raw data as JSON
            user.setRawData(objectMapper.writeValueAsString(userData));

            fetchedUserRepository.save(user);
            log.debug("Saved user: sourceName={}, externalId={}", sourceName, externalId);
            return true;

        } catch (final Exception e) {
            log.error("Failed to save user from source: {}, error={}", sourceName, e.getMessage(), e);
            return false;
        }
    }

    private String extractRequiredField(final Map<String, Object> data, final String fieldName) {
        final Object value = data.get(fieldName);
        if (value == null) {
            throw new IllegalArgumentException("Required field missing: " + fieldName);
        }
        return value.toString();
    }

    private String extractField(final Map<String, Object> data, final String fieldName) {
        final Object value = data.get(fieldName);
        return value != null ? value.toString() : null;
    }
}

