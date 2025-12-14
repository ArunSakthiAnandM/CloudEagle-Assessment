package cloud.eagle.assessment.controller;

import cloud.eagle.assessment.domain.dto.ApiConfigurationDto;
import cloud.eagle.assessment.domain.dto.FetchedUserDto;
import cloud.eagle.assessment.domain.dto.FetchUsersRequest;
import cloud.eagle.assessment.domain.dto.FetchUsersResponse;
import cloud.eagle.assessment.service.ApiConfigService;
import cloud.eagle.assessment.service.UserFetchService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for user integration operations.
 * Thin controller following layered architecture: delegates to service layer.
 */
@RestController
@RequestMapping("/api/v1/integrations")
@Slf4j
public class UserIntegrationController {

    private final UserFetchService userFetchService;
    private final ApiConfigService apiConfigService;

    public UserIntegrationController(
        final UserFetchService userFetchService,
        final ApiConfigService apiConfigService
    ) {
        this.userFetchService = userFetchService;
        this.apiConfigService = apiConfigService;
    }

    /**
     * Trigger user fetch from a specific external source.
     *
     * @param request the fetch request containing source name
     * @return response with fetch statistics
     */
    @PostMapping("/fetch")
    public ResponseEntity<FetchUsersResponse> fetchUsers(@Valid @RequestBody final FetchUsersRequest request) {
        log.info("Received fetch request for source: {}", request.sourceName());
        final FetchUsersResponse response = userFetchService.fetchUsersFromSource(request.sourceName());
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    /**
     * Get all fetched users with pagination.
     *
     * @param pageable pagination parameters
     * @return page of fetched users
     */
    @GetMapping("/users")
    public ResponseEntity<Page<FetchedUserDto>> getAllUsers(
        @PageableDefault(size = 20) final Pageable pageable
    ) {
        log.debug("Retrieving all users, page: {}", pageable.getPageNumber());
        final Page<FetchedUserDto> users = userFetchService.getAllUsers(pageable);
        return ResponseEntity.ok(users);
    }

    /**
     * Get fetched users by source name with pagination.
     *
     * @param sourceName the source name
     * @param pageable pagination parameters
     * @return page of fetched users from the specified source
     */
    @GetMapping("/users/{sourceName}")
    public ResponseEntity<Page<FetchedUserDto>> getUsersBySource(
        @PathVariable final String sourceName,
        @PageableDefault(size = 20) final Pageable pageable
    ) {
        log.debug("Retrieving users from source: {}, page: {}", sourceName, pageable.getPageNumber());
        final Page<FetchedUserDto> users = userFetchService.getUsersBySource(sourceName, pageable);
        return ResponseEntity.ok(users);
    }

    /**
     * Get all API configurations.
     *
     * @return list of available integrations
     */
    @GetMapping("/configs")
    public ResponseEntity<List<ApiConfigurationDto>> getAllConfigurations() {
        log.debug("Retrieving all API configurations");
        final List<ApiConfigurationDto> configs = apiConfigService.getAllConfigurations();
        return ResponseEntity.ok(configs);
    }

    /**
     * Get API configuration by source name.
     *
     * @param sourceName the source name
     * @return configuration details
     */
    @GetMapping("/configs/{sourceName}")
    public ResponseEntity<ApiConfigurationDto> getConfiguration(@PathVariable final String sourceName) {
        log.debug("Retrieving configuration for source: {}", sourceName);
        final ApiConfigurationDto config = apiConfigService.getConfiguration(sourceName);
        return ResponseEntity.ok(config);
    }
}

