package com.mortenm12.authservice.api.dto.role;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record AssignRoleRequest(
        @NotNull(message = "userId must not be null")
        UUID userId,

        @NotNull(message = "roleId must not be null")
        UUID roleId
) { }
