package cloud.eagle.assessment.domain.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Entity storing dynamic API configuration for external integrations.
 * Each configuration defines how to call a third-party API endpoint.
 */
@Entity
@Table(name = "api_configurations")
@Getter
@Setter
@NoArgsConstructor
public class ApiConfiguration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false, unique = true)
    private String sourceName;

    @NotBlank
    @Column(nullable = false)
    private String endpointUrl;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private HttpMethod httpMethod;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AuthType authType;

    @Column(length = 1000)
    private String authCredentials;

    @Column(length = 2000)
    private String requestHeaders;

    @Column(length = 500)
    private String responseRootPath;

    @Column(nullable = false)
    private boolean active = true;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant updatedAt;

    @OneToMany(mappedBy = "apiConfiguration", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FieldMapping> fieldMappings = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        final Instant now = Instant.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
    }

    public void addFieldMapping(final FieldMapping fieldMapping) {
        fieldMappings.add(fieldMapping);
        fieldMapping.setApiConfiguration(this);
    }

    public void removeFieldMapping(final FieldMapping fieldMapping) {
        fieldMappings.remove(fieldMapping);
        fieldMapping.setApiConfiguration(null);
    }
}
