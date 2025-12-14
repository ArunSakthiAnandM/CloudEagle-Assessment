package cloud.eagle.assessment.service;

import cloud.eagle.assessment.domain.dto.ApiConfigurationDto;
import cloud.eagle.assessment.domain.entity.ApiConfiguration;
import cloud.eagle.assessment.exception.ApiConfigurationNotFoundException;
import cloud.eagle.assessment.mapper.EntityMapper;
import cloud.eagle.assessment.repository.ApiConfigurationRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service for managing API configurations.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
public class ApiConfigService {

    private final ApiConfigurationRepository repository;

    public ApiConfigService(final ApiConfigurationRepository repository) {
        this.repository = repository;
    }

    /**
     * Find active API configuration by source name.
     *
     * @param sourceName the source name
     * @return API configuration
     * @throws ApiConfigurationNotFoundException if not found
     */
    public ApiConfiguration findActiveConfiguration(final String sourceName) {
        log.debug("Finding active configuration for source: {}", sourceName);
        return repository.findBySourceNameAndActive(sourceName, true)
            .orElseThrow(() -> new ApiConfigurationNotFoundException(sourceName));
    }

    /**
     * Get all API configurations.
     *
     * @return list of configuration DTOs
     */
    public List<ApiConfigurationDto> getAllConfigurations() {
        log.debug("Retrieving all API configurations");
        final List<ApiConfiguration> configs = repository.findAll();
        return EntityMapper.toConfigDtoList(configs);
    }

    /**
     * Get configuration by source name.
     *
     * @param sourceName the source name
     * @return configuration DTO
     * @throws ApiConfigurationNotFoundException if not found
     */
    public ApiConfigurationDto getConfiguration(final String sourceName) {
        log.debug("Retrieving configuration for source: {}", sourceName);
        final ApiConfiguration config = repository.findBySourceName(sourceName)
            .orElseThrow(() -> new ApiConfigurationNotFoundException(sourceName));
        return EntityMapper.toDto(config);
    }

    /**
     * Save or update API configuration.
     *
     * @param configuration the configuration to save
     * @return saved configuration
     */
    @Transactional
    public ApiConfiguration saveConfiguration(final ApiConfiguration configuration) {
        log.info("Saving API configuration for source: {}", configuration.getSourceName());
        return repository.save(configuration);
    }
}
