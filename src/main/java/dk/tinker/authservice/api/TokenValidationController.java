package dk.tinker.authservice.api;

import dk.tinker.authservice.api.dto.token.ValidateTokenRequest;
import dk.tinker.authservice.api.dto.token.ValidationResponse;
import dk.tinker.authservice.service.TokenValidationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/tokens")
@Tag(name = "Token Validation", description = "Used by microservices to validate JWT and API key tokens")
public class TokenValidationController {

    private final TokenValidationService tokenValidationService;

    public TokenValidationController(TokenValidationService tokenValidationService) {
        this.tokenValidationService = tokenValidationService;
    }

    @PostMapping("/validate")
    @Operation(summary = "Validate a JWT or API key token")
    public ValidationResponse validate(@Valid @RequestBody ValidateTokenRequest request) {
        return tokenValidationService.validate(request.token(), request.tokenType());
    }
}
