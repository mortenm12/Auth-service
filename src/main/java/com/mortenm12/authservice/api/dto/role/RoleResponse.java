package com.mortenm12.authservice.api.dto.role;

import com.mortenm12.authservice.domain.Role;

import java.time.Instant;
import java.util.UUID;

public record RoleResponse(
        UUID id,
        String name,
        String description,
        Instant createdAt
) {
    public static RoleResponse from(Role role) {
        return new RoleResponse(
                role.getId(),
                role.getName(),
                role.getDescription(),
                role.getCreatedAt()
        );
    }
}
