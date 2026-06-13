package dk.tinker.authservice.api.dto.apikey;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record CreateApiKeyRequest(
        @NotBlank(message = "Name must not be blank")
        @Size(max = 255)
        String name,

        List<String> scopes,

        UUID organizationId,

        UUID userId,

        Instant expiresAt
) { }
