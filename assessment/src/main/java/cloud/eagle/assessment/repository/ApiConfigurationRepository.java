package cloud.eagle.assessment.repository;

import cloud.eagle.assessment.domain.entity.ApiConfiguration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for ApiConfiguration entity.
 */
@Repository
public interface ApiConfigurationRepository extends JpaRepository<ApiConfiguration, Long> {

    /**
     * Find API configuration by source name.
     *
     * @param sourceName the source name
     * @return optional configuration
     */
    Optional<ApiConfiguration> findBySourceName(String sourceName);

    /**
     * Find active API configuration by source name.
     *
     * @param sourceName the source name
     * @param active the active flag
     * @return optional configuration
     */
    Optional<ApiConfiguration> findBySourceNameAndActive(String sourceName, boolean active);
}

