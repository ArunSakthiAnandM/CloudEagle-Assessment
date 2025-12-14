package cloud.eagle.assessment.service;

import cloud.eagle.assessment.domain.entity.ApiConfiguration;
import cloud.eagle.assessment.domain.entity.AuthType;
import cloud.eagle.assessment.domain.entity.FieldMapping;
import cloud.eagle.assessment.exception.ExternalApiException;
import cloud.eagle.assessment.exception.FieldMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Generic service to call any external API and parse responses.
 * Uses WebClient with virtual threads for blocking I/O operations.
 */
@Service
@Slf4j
public class ExternalApiService {

    private final WebClient.Builder webClientBuilder;
    private final ObjectMapper objectMapper;

    public ExternalApiService(final WebClient.Builder webClientBuilder, final ObjectMapper objectMapper) {
        this.webClientBuilder = webClientBuilder;
        this.objectMapper = objectMapper;
    }

    /**
     * Calls external API and returns raw JSON response.
     *
     * @param config API configuration
     * @return raw JSON response as string
     */
    public String callExternalApi(final ApiConfiguration config) {
        log.info("Calling external API: sourceName={}, url={}", config.getSourceName(), config.getEndpointUrl());

        final WebClient webClient = buildWebClient(config);

        try {
            final String response = switch (config.getHttpMethod()) {
                case GET -> webClient.get()
                    .uri(config.getEndpointUrl())
                    .retrieve()
                    .bodyToMono(String.class)
                    .timeout(Duration.ofSeconds(30))
                    .onErrorResume(this::handleApiError)
                    .block();
                case POST -> webClient.post()
                    .uri(config.getEndpointUrl())
                    .retrieve()
                    .bodyToMono(String.class)
                    .timeout(Duration.ofSeconds(30))
                    .onErrorResume(this::handleApiError)
                    .block();
                default -> throw new ExternalApiException("Unsupported HTTP method: " + config.getHttpMethod());
            };

            log.info("Successfully received response from: sourceName={}", config.getSourceName());
            return response;

        } catch (final Exception e) {
            log.error("Failed to call external API: sourceName={}, error={}", config.getSourceName(), e.getMessage(), e);
            throw new ExternalApiException("Failed to call external API for source: " + config.getSourceName(), e);
        }
    }

    /**
     * Parses JSON response and extracts field values based on field mappings.
     *
     * @param jsonResponse raw JSON response
     * @param config API configuration with field mappings
     * @return list of mapped user data
     */
    public List<Map<String, Object>> parseAndMapResponse(final String jsonResponse, final ApiConfiguration config) {
        log.info("Parsing response for source: {}", config.getSourceName());

        try {
            final Object document = JsonPath.parse(jsonResponse).json();
            final List<Map<String, Object>> results = new ArrayList<>();

            // Get the root path for the response (e.g., "$.collection" for Calendly)
            final String rootPath = config.getResponseRootPath() != null
                ? config.getResponseRootPath()
                : "$";

            final Object rootData = JsonPath.read(document, rootPath);

            // If root data is a list, process each item
            if (rootData instanceof List<?> items) {
                for (final Object item : items) {
                    results.add(mapSingleItem(item, config.getFieldMappings()));
                }
            } else {
                // Single object response
                results.add(mapSingleItem(rootData, config.getFieldMappings()));
            }

            log.info("Successfully parsed {} items from source: {}", results.size(), config.getSourceName());
            return results;

        } catch (final Exception e) {
            log.error("Failed to parse response for source: {}, error={}", config.getSourceName(), e.getMessage(), e);
            throw new FieldMappingException("Failed to parse response for source: " + config.getSourceName(), e);
        }
    }

    private Map<String, Object> mapSingleItem(final Object item, final List<FieldMapping> fieldMappings) {
        final Map<String, Object> mappedData = new HashMap<>();

        for (final FieldMapping mapping : fieldMappings) {
            try {
                // Extract value using JsonPath relative to the item
                final Object value = JsonPath.read(item, mapping.getJsonPath());
                mappedData.put(mapping.getInternalFieldName(), value);
            } catch (final Exception e) {
                if (mapping.isRequired()) {
                    throw new FieldMappingException(
                        "Required field mapping failed: " + mapping.getInternalFieldName(), e);
                }
                // Use default value if provided
                if (mapping.getDefaultValue() != null) {
                    mappedData.put(mapping.getInternalFieldName(), mapping.getDefaultValue());
                }
                log.debug("Optional field not found: {}", mapping.getInternalFieldName());
            }
        }

        return mappedData;
    }

    private WebClient buildWebClient(final ApiConfiguration config) {
        final WebClient.Builder builder = webClientBuilder.clone();

        // Apply authentication based on auth type
        switch (config.getAuthType()) {
            case BEARER_TOKEN -> {
                if (config.getAuthCredentials() != null) {
                    builder.defaultHeader("Authorization", "Bearer " + config.getAuthCredentials());
                }
            }
            case API_KEY -> {
                if (config.getAuthCredentials() != null) {
                    builder.defaultHeader("Authorization", config.getAuthCredentials());
                }
            }
            case BASIC_AUTH -> {
                // Basic auth would require parsing credentials
                log.warn("Basic auth not fully implemented yet");
            }
            case NONE -> {
                // No authentication
            }
        }

        // Apply custom headers if provided
        if (config.getRequestHeaders() != null && !config.getRequestHeaders().isBlank()) {
            applyCustomHeaders(builder, config.getRequestHeaders());
        }

        return builder.build();
    }

    private void applyCustomHeaders(final WebClient.Builder builder, final String headersJson) {
        try {
            @SuppressWarnings("unchecked")
            final Map<String, String> headers = objectMapper.readValue(headersJson, Map.class);
            headers.forEach(builder::defaultHeader);
        } catch (final Exception e) {
            log.warn("Failed to parse custom headers, skipping: {}", e.getMessage());
        }
    }

    private Mono<String> handleApiError(final Throwable error) {
        log.error("API call error: {}", error.getMessage());
        return Mono.error(new ExternalApiException("External API call failed", error));
    }
}

