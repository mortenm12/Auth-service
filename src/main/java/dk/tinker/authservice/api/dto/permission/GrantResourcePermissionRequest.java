package dk.tinker.authservice.api.dto.permission;

import dk.tinker.authservice.domain.PermissionLevel;

import java.util.UUID;

public record GrantResourcePermissionRequest(
        UUID userId,
        String resourceType,
        String resourceId,
        PermissionLevel level,
        UUID grantedBy
) {}
