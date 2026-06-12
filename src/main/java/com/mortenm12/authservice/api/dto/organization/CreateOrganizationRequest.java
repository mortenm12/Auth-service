package com.mortenm12.authservice.api.dto.organization;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record CreateOrganizationRequest(
        @NotBlank(message = "Name must not be blank")
        @Size(max = 255)
        String name,

        @NotBlank(message = "Slug must not be blank")
        @Size(max = 100)
        @Pattern(regexp = "^[a-z0-9-]+$", message = "Slug may only contain lowercase letters, digits and hyphens")
        String slug,

        UUID parentId
) { }
