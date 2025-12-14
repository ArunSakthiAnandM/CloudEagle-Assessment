package cloud.eagle.assessment.config;

import cloud.eagle.assessment.domain.entity.ApiConfiguration;
import cloud.eagle.assessment.domain.entity.AuthType;
import cloud.eagle.assessment.domain.entity.FieldMapping;
import cloud.eagle.assessment.domain.entity.HttpMethod;
import cloud.eagle.assessment.repository.ApiConfigurationRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * Database initialization to seed Calendly API configuration.
 * Runs on application startup to create default integration configurations.
 */
@Configuration
@Slf4j
public class DataInitializer {

    @Bean
    @Profile("!test")
    public CommandLineRunner initializeDatabase(final ApiConfigurationRepository repository) {
        return args -> {
            log.info("Initializing database with API configurations...");

            // Check if Calendly config already exists
            if (repository.findBySourceName("calendly").isPresent()) {
                log.info("Calendly configuration already exists, skipping initialization");
                return;
            }

            // Create Calendly API configuration
            final ApiConfiguration calendlyConfig = new ApiConfiguration();
            calendlyConfig.setSourceName("calendly");
            calendlyConfig.setEndpointUrl("https://api.calendly.com/users");
            calendlyConfig.setHttpMethod(HttpMethod.GET);
            calendlyConfig.setAuthType(AuthType.BEARER_TOKEN);
            // Note: In production, this would come from environment variables or secure vault
            calendlyConfig.setAuthCredentials("YOUR_CALENDLY_API_TOKEN");
            calendlyConfig.setResponseRootPath("$.collection");
            calendlyConfig.setActive(true);

            // Add field mappings for Calendly user data
            final FieldMapping uriMapping = new FieldMapping("externalId", "$.uri", true);
            final FieldMapping nameMapping = new FieldMapping("name", "$.name", false);
            final FieldMapping emailMapping = new FieldMapping("email", "$.email", false);
            final FieldMapping timezoneMapping = new FieldMapping("timezone", "$.timezone", false);
            final FieldMapping avatarMapping = new FieldMapping("avatarUrl", "$.avatar_url", false);
            final FieldMapping createdAtMapping = new FieldMapping("createdAt", "$.created_at", false);

            calendlyConfig.addFieldMapping(uriMapping);
            calendlyConfig.addFieldMapping(nameMapping);
            calendlyConfig.addFieldMapping(emailMapping);
            calendlyConfig.addFieldMapping(timezoneMapping);
            calendlyConfig.addFieldMapping(avatarMapping);
            calendlyConfig.addFieldMapping(createdAtMapping);

            repository.save(calendlyConfig);
            log.info("Successfully initialized Calendly API configuration");

            // You can add more integrations here (Dropbox, etc.)
            log.info("Database initialization completed");
        };
    }
}

