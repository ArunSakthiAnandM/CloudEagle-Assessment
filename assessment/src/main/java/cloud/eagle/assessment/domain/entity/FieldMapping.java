package cloud.eagle.assessment.domain.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entity defining how to map external API fields to internal user fields.
 * Uses JsonPath expressions to extract values from API responses.
 */
@Entity
@Table(name = "field_mappings")
@Getter
@Setter
@NoArgsConstructor
public class FieldMapping {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "api_configuration_id", nullable = false)
    private ApiConfiguration apiConfiguration;

    @NotBlank
    @Column(nullable = false)
    private String internalFieldName;

    @NotBlank
    @Column(nullable = false, length = 500)
    private String jsonPath;

    @Column(length = 100)
    private String defaultValue;

    @Column(nullable = false)
    private boolean required = false;

    public FieldMapping(final String internalFieldName, final String jsonPath, final boolean required) {
        this.internalFieldName = internalFieldName;
        this.jsonPath = jsonPath;
        this.required = required;
    }
}

