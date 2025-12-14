package cloud.eagle.assessment.domain.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

/**
 * Temporary storage for users fetched from external systems.
 * Normalized representation across different sources.
 */
@Entity
@Table(name = "fetched_users", indexes = {
    @Index(name = "idx_source_external_id", columnList = "sourceName,externalId", unique = true)
})
@Getter
@Setter
@NoArgsConstructor
public class FetchedUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false)
    private String sourceName;

    @NotBlank
    @Column(nullable = false)
    private String externalId;

    private String email;

    private String name;

    private String firstName;

    private String lastName;

    private String timezone;

    private String avatarUrl;

    @Column(length = 2000)
    private String rawData;

    @Column(nullable = false, updatable = false)
    private Instant fetchedAt;

    @PrePersist
    protected void onCreate() {
        fetchedAt = Instant.now();
    }

    public FetchedUser(final String sourceName, final String externalId) {
        this.sourceName = sourceName;
        this.externalId = externalId;
    }
}

