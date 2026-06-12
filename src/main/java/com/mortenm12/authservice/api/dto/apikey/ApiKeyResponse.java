package com.mortenm12.authservice.api.dto.apikey;

import com.mortenm12.authservice.domain.ApiKey;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record ApiKeyResponse(
        UUID id,
        String name,
        String keyPrefix,
        List<String> scopes,
        UUID organizationId,
        UUID userId,
        Instant expiresAt,
        Instant lastUsedAt,
        Instant createdAt
) {
    public static ApiKeyResponse from(ApiKey apiKey) {
        return new ApiKeyResponse(
                apiKey.getId(),
                apiKey.getName(),
                apiKey.getKeyPrefix(),
                apiKey.getScopesAsList(),
                apiKey.getOrganization() != null ? apiKey.getOrganization().getId() : null,
                apiKey.getUser() != null ? apiKey.getUser().getId() : null,
                apiKey.getExpiresAt(),
                apiKey.getLastUsedAt(),
                apiKey.getCreatedAt()
        );
    }
}
