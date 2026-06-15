package dk.tinker.authservice.api.dto.apikey;

import dk.tinker.authservice.domain.ApiKey;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record ApiKeyCreatedResponse(
        UUID id,
        String name,
        String keyPrefix,
        String rawKey,
        List<String> scopes,
        UUID organizationId,
        UUID userId,
        Instant expiresAt,
        Instant createdAt
) {
    public static ApiKeyCreatedResponse from(ApiKey apiKey, String rawKey) {
        return new ApiKeyCreatedResponse(
                apiKey.getId(),
                apiKey.getName(),
                apiKey.getKeyPrefix(),
                rawKey,
                apiKey.getScopesAsList(),
                apiKey.getOrganization() != null ? apiKey.getOrganization().getId() : null,
                apiKey.getUser() != null ? apiKey.getUser().getId() : null,
                apiKey.getExpiresAt(),
                apiKey.getCreatedAt()
        );
    }
}
