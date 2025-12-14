package cloud.eagle.assessment.mapper;

import cloud.eagle.assessment.domain.dto.ApiConfigurationDto;
import cloud.eagle.assessment.domain.dto.FetchedUserDto;
import cloud.eagle.assessment.domain.dto.FieldMappingDto;
import cloud.eagle.assessment.domain.entity.ApiConfiguration;
import cloud.eagle.assessment.domain.entity.FetchedUser;
import cloud.eagle.assessment.domain.entity.FieldMapping;

import java.util.List;

/**
 * Manual mapper for entity to DTO conversions.
 * Following starter.md guidelines: DO isolate mapping via mappers.
 */
public final class EntityMapper {

    private EntityMapper() {
        // Utility class
    }

    public static FetchedUserDto toDto(final FetchedUser entity) {
        if (entity == null) {
            return null;
        }
        return new FetchedUserDto(
            entity.getId(),
            entity.getSourceName(),
            entity.getExternalId(),
            entity.getEmail(),
            entity.getName(),
            entity.getFirstName(),
            entity.getLastName(),
            entity.getTimezone(),
            entity.getAvatarUrl(),
            entity.getFetchedAt()
        );
    }

    public static List<FetchedUserDto> toUserDtoList(final List<FetchedUser> entities) {
        return entities.stream()
            .map(EntityMapper::toDto)
            .toList();
    }

    public static ApiConfigurationDto toDto(final ApiConfiguration entity) {
        if (entity == null) {
            return null;
        }
        return new ApiConfigurationDto(
            entity.getId(),
            entity.getSourceName(),
            entity.getEndpointUrl(),
            entity.getHttpMethod(),
            entity.getAuthType(),
            entity.getRequestHeaders(),
            entity.getResponseRootPath(),
            entity.isActive(),
            toFieldMappingDtoList(entity.getFieldMappings()),
            entity.getCreatedAt(),
            entity.getUpdatedAt()
        );
    }

    public static List<ApiConfigurationDto> toConfigDtoList(final List<ApiConfiguration> entities) {
        return entities.stream()
            .map(EntityMapper::toDto)
            .toList();
    }

    public static FieldMappingDto toDto(final FieldMapping entity) {
        if (entity == null) {
            return null;
        }
        return new FieldMappingDto(
            entity.getId(),
            entity.getInternalFieldName(),
            entity.getJsonPath(),
            entity.getDefaultValue(),
            entity.isRequired()
        );
    }

    public static List<FieldMappingDto> toFieldMappingDtoList(final List<FieldMapping> entities) {
        return entities.stream()
            .map(EntityMapper::toDto)
            .toList();
    }
}

