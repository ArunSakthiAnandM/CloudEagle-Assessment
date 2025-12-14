package cloud.eagle.assessment.service;

import cloud.eagle.assessment.domain.dto.ApiConfigurationDto;
import cloud.eagle.assessment.domain.entity.ApiConfiguration;
import cloud.eagle.assessment.exception.ApiConfigurationNotFoundException;
import cloud.eagle.assessment.repository.ApiConfigurationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

/**
 * Unit tests for ApiConfigService.
 */
@ExtendWith(MockitoExtension.class)
class ApiConfigServiceTest {

    @Mock
    private ApiConfigurationRepository repository;

    @InjectMocks
    private ApiConfigService apiConfigService;

    private ApiConfiguration testConfig;

    @BeforeEach
    void setUp() {
        testConfig = new ApiConfiguration();
        testConfig.setId(1L);
        testConfig.setSourceName("test-source");
        testConfig.setActive(true);
    }

    @Test
    void findActiveConfiguration_withValidSource_shouldReturnConfig() {
        // Given
        when(repository.findBySourceNameAndActive("test-source", true))
            .thenReturn(Optional.of(testConfig));

        // When
        final ApiConfiguration result = apiConfigService.findActiveConfiguration("test-source");

        // Then
        assertNotNull(result);
        assertEquals("test-source", result.getSourceName());
        assertTrue(result.isActive());
    }

    @Test
    void findActiveConfiguration_withInvalidSource_shouldThrowException() {
        // Given
        when(repository.findBySourceNameAndActive(anyString(), eq(true)))
            .thenReturn(Optional.empty());

        // When & Then
        assertThrows(ApiConfigurationNotFoundException.class, () ->
            apiConfigService.findActiveConfiguration("invalid-source"));
    }

    @Test
    void getAllConfigurations_shouldReturnConfigList() {
        // Given
        when(repository.findAll()).thenReturn(List.of(testConfig));

        // When
        final List<ApiConfigurationDto> results = apiConfigService.getAllConfigurations();

        // Then
        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals("test-source", results.get(0).sourceName());
    }

    @Test
    void getConfiguration_withValidSource_shouldReturnDto() {
        // Given
        when(repository.findBySourceName("test-source"))
            .thenReturn(Optional.of(testConfig));

        // When
        final ApiConfigurationDto result = apiConfigService.getConfiguration("test-source");

        // Then
        assertNotNull(result);
        assertEquals("test-source", result.sourceName());
    }

    @Test
    void saveConfiguration_shouldReturnSavedConfig() {
        // Given
        when(repository.save(testConfig)).thenReturn(testConfig);

        // When
        final ApiConfiguration result = apiConfigService.saveConfiguration(testConfig);

        // Then
        assertNotNull(result);
        assertEquals(testConfig.getId(), result.getId());
    }
}

