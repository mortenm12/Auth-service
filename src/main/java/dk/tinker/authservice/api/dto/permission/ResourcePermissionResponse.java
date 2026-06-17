package dk.tinker.authservice.api.dto.permission;

import dk.tinker.authservice.domain.PermissionLevel;

import java.time.Instant;
import java.util.UUID;

public record ResourcePermissionResponse(
        UUID id,
        UUID userId,
        String resourceType,
        String resourceId,
        PermissionLevel level,
        UUID grantedBy,
        Instant createdAt
) {}
