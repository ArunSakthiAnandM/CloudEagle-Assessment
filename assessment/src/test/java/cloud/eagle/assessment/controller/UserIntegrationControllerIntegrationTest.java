package cloud.eagle.assessment.controller;

import cloud.eagle.assessment.domain.entity.ApiConfiguration;
import cloud.eagle.assessment.domain.entity.AuthType;
import cloud.eagle.assessment.domain.entity.FieldMapping;
import cloud.eagle.assessment.domain.entity.HttpMethod;
import cloud.eagle.assessment.repository.ApiConfigurationRepository;
import cloud.eagle.assessment.repository.FetchedUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for UserIntegrationController.
 * Tests full application context loading and repository integration.
 */
@SpringBootTest
@ActiveProfiles("test")
class UserIntegrationControllerIntegrationTest {

    @Autowired
    private ApiConfigurationRepository apiConfigRepository;

    @Autowired
    private FetchedUserRepository fetchedUserRepository;

    @Autowired
    private UserIntegrationController controller;

    @BeforeEach
    void setUp() {
        // Clean database
        fetchedUserRepository.deleteAll();
        apiConfigRepository.deleteAll();
    }

    @Test
    void contextLoads() {
        // Verify Spring context loads successfully
        assertNotNull(controller);
        assertNotNull(apiConfigRepository);
        assertNotNull(fetchedUserRepository);
    }

    @Test
    void getAllConfigurations_shouldReturnEmptyListWhenNoConfigs() {
        // When
        var response = controller.getAllConfigurations();

        // Then
        assertNotNull(response);
        assertTrue(response.getBody().isEmpty());
    }

    @Test
    void saveAndRetrieveConfiguration() {
        // Given
        final ApiConfiguration config = new ApiConfiguration();
        config.setSourceName("test-source");
        config.setEndpointUrl("https://api.example.com/users");
        config.setHttpMethod(HttpMethod.GET);
        config.setAuthType(AuthType.BEARER_TOKEN);
        config.setAuthCredentials("test-token");
        config.setResponseRootPath("$.data");
        config.setActive(true);

        final FieldMapping mapping = new FieldMapping("externalId", "$.id", true);
        config.addFieldMapping(mapping);

        // When
        final ApiConfiguration saved = apiConfigRepository.save(config);

        // Then
        assertNotNull(saved.getId());
        assertEquals("test-source", saved.getSourceName());
        assertEquals(1, saved.getFieldMappings().size());

        // Verify retrieval
        final var found = apiConfigRepository.findBySourceName("test-source");
        assertTrue(found.isPresent());
        assertEquals("test-source", found.get().getSourceName());
    }
}

