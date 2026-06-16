package dk.tinker.authservice.api.dto.token;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ValidateTokenRequest(
        @NotBlank String token,
        @NotNull TokenTypeDto tokenType
) { }
