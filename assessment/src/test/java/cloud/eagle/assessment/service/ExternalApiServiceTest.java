package cloud.eagle.assessment.service;

import cloud.eagle.assessment.domain.entity.ApiConfiguration;
import cloud.eagle.assessment.domain.entity.AuthType;
import cloud.eagle.assessment.domain.entity.FieldMapping;
import cloud.eagle.assessment.domain.entity.HttpMethod;
import cloud.eagle.assessment.exception.ExternalApiException;
import cloud.eagle.assessment.exception.FieldMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for ExternalApiService.
 * Uses MockWebServer to simulate external API responses.
 */
class ExternalApiServiceTest {

    private MockWebServer mockWebServer;
    private ExternalApiService externalApiService;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();

        objectMapper = new ObjectMapper();
        final WebClient.Builder webClientBuilder = WebClient.builder();
        externalApiService = new ExternalApiService(webClientBuilder, objectMapper);
    }

    @AfterEach
    void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @Test
    void callExternalApi_withValidConfig_shouldReturnResponse() throws InterruptedException {
        // Given
        final String mockResponse = "{\"data\": \"test\"}";
        mockWebServer.enqueue(new MockResponse()
            .setBody(mockResponse)
            .setResponseCode(200));

        final ApiConfiguration config = createTestConfig();
        config.setEndpointUrl(mockWebServer.url("/api/users").toString());

        // When
        final String response = externalApiService.callExternalApi(config);

        // Then
        assertNotNull(response);
        assertEquals(mockResponse, response);

        final RecordedRequest request = mockWebServer.takeRequest();
        assertEquals("GET", request.getMethod());
        assertEquals("Bearer test-token", request.getHeader("Authorization"));
    }

    @Test
    void callExternalApi_withServerError_shouldThrowException() {
        // Given
        mockWebServer.enqueue(new MockResponse().setResponseCode(500));

        final ApiConfiguration config = createTestConfig();
        config.setEndpointUrl(mockWebServer.url("/api/users").toString());

        // When & Then
        assertThrows(ExternalApiException.class, () ->
            externalApiService.callExternalApi(config));
    }

    @Test
    void parseAndMapResponse_withValidJson_shouldMapFields() {
        // Given
        final String jsonResponse = """
            {
                "collection": [
                    {
                        "uri": "user-123",
                        "name": "John Doe",
                        "email": "john@example.com"
                    }
                ]
            }
            """;

        final ApiConfiguration config = createTestConfig();
        config.setResponseRootPath("$.collection");

        final FieldMapping uriMapping = new FieldMapping("externalId", "$.uri", true);
        final FieldMapping nameMapping = new FieldMapping("name", "$.name", false);
        final FieldMapping emailMapping = new FieldMapping("email", "$.email", false);

        config.addFieldMapping(uriMapping);
        config.addFieldMapping(nameMapping);
        config.addFieldMapping(emailMapping);

        // When
        final List<Map<String, Object>> results = externalApiService.parseAndMapResponse(jsonResponse, config);

        // Then
        assertEquals(1, results.size());
        final Map<String, Object> user = results.get(0);
        assertEquals("user-123", user.get("externalId"));
        assertEquals("John Doe", user.get("name"));
        assertEquals("john@example.com", user.get("email"));
    }

    @Test
    void parseAndMapResponse_withMissingRequiredField_shouldThrowException() {
        // Given
        final String jsonResponse = """
            {
                "collection": [
                    {
                        "name": "John Doe"
                    }
                ]
            }
            """;

        final ApiConfiguration config = createTestConfig();
        config.setResponseRootPath("$.collection");

        final FieldMapping uriMapping = new FieldMapping("externalId", "$.uri", true);
        config.addFieldMapping(uriMapping);

        // When & Then
        assertThrows(FieldMappingException.class, () ->
            externalApiService.parseAndMapResponse(jsonResponse, config));
    }

    @Test
    void parseAndMapResponse_withMissingOptionalField_shouldUseDefaultValue() {
        // Given
        final String jsonResponse = """
            {
                "collection": [
                    {
                        "uri": "user-123",
                        "name": "John Doe"
                    }
                ]
            }
            """;

        final ApiConfiguration config = createTestConfig();
        config.setResponseRootPath("$.collection");

        final FieldMapping uriMapping = new FieldMapping("externalId", "$.uri", true);
        final FieldMapping emailMapping = new FieldMapping("email", "$.email", false);
        emailMapping.setDefaultValue("no-email@example.com");

        config.addFieldMapping(uriMapping);
        config.addFieldMapping(emailMapping);

        // When
        final List<Map<String, Object>> results = externalApiService.parseAndMapResponse(jsonResponse, config);

        // Then
        assertEquals(1, results.size());
        final Map<String, Object> user = results.get(0);
        assertEquals("user-123", user.get("externalId"));
        assertEquals("no-email@example.com", user.get("email"));
    }

    private ApiConfiguration createTestConfig() {
        final ApiConfiguration config = new ApiConfiguration();
        config.setSourceName("test");
        config.setEndpointUrl("http://localhost:8080/api/users");
        config.setHttpMethod(HttpMethod.GET);
        config.setAuthType(AuthType.BEARER_TOKEN);
        config.setAuthCredentials("test-token");
        config.setActive(true);
        return config;
    }
}

