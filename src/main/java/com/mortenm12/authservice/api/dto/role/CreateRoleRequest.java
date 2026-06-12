package com.mortenm12.authservice.api.dto.role;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateRoleRequest(
        @NotBlank(message = "Name must not be blank")
        @Size(max = 100)
        String name,

        @Size(max = 500)
        String description
) { }
