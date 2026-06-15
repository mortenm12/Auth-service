package dk.tinker.authservice.api;

import dk.tinker.authservice.api.dto.apikey.ValidateApiKeyRequest;
import dk.tinker.authservice.api.dto.apikey.ValidateApiKeyResponse;
import dk.tinker.authservice.service.ApiKeyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal")
@Tag(name = "Internal", description = "Internal service-to-service endpoints (require X-Internal-Secret header)")
public class InternalTokenController {

    private final ApiKeyService apiKeyService;

    public InternalTokenController(ApiKeyService apiKeyService) {
        this.apiKeyService = apiKeyService;
    }

    @PostMapping("/api-keys/validate")
    @Operation(summary = "Validate an API key")
    public ResponseEntity<ValidateApiKeyResponse> validateApiKey(
            @Valid @RequestBody ValidateApiKeyRequest request) {
        return ResponseEntity.ok(apiKeyService.validateForExternal(request.rawKey()));
    }
}
