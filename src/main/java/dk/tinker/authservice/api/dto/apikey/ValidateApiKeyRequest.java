package dk.tinker.authservice.api.dto.apikey;

import jakarta.validation.constraints.NotBlank;

public record ValidateApiKeyRequest(@NotBlank String rawKey) { }
