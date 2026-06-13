package dk.tinker.authservice.api.dto.organization;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateOrganizationRequest(
        @NotBlank(message = "Name must not be blank")
        @Size(max = 255)
        String name
) { }
