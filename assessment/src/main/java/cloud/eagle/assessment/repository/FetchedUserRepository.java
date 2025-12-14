package cloud.eagle.assessment.repository;

import cloud.eagle.assessment.domain.entity.FetchedUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for FetchedUser entity.
 */
@Repository
public interface FetchedUserRepository extends JpaRepository<FetchedUser, Long> {

    /**
     * Find fetched user by source name and external ID.
     *
     * @param sourceName the source name
     * @param externalId the external ID
     * @return optional fetched user
     */
    Optional<FetchedUser> findBySourceNameAndExternalId(String sourceName, String externalId);

    /**
     * Find all fetched users by source name with pagination.
     *
     * @param sourceName the source name
     * @param pageable pagination information
     * @return page of fetched users
     */
    Page<FetchedUser> findBySourceName(String sourceName, Pageable pageable);

    /**
     * Check if a user exists by source name and external ID.
     *
     * @param sourceName the source name
     * @param externalId the external ID
     * @return true if exists
     */
    boolean existsBySourceNameAndExternalId(String sourceName, String externalId);
}

