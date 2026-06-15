package dk.tinker.authservice.api.dto.apikey;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record ValidateApiKeyResponse(
        boolean valid,
        String keyHash,
        UUID userId,
        UUID orgId,
        List<String> scopes,
        Instant expiresAt
) { }
