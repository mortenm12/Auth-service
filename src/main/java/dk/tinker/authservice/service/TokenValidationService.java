package dk.tinker.authservice.service;

import dk.tinker.authservice.api.dto.token.TokenTypeDto;
import dk.tinker.authservice.api.dto.token.ValidationResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class TokenValidationService {

    private static final Logger log = LoggerFactory.getLogger(TokenValidationService.class);

    private final JwtDecoder jwtDecoder;
    private final ApiKeyService apiKeyService;

    public TokenValidationService(JwtDecoder jwtDecoder, ApiKeyService apiKeyService) {
        this.jwtDecoder = jwtDecoder;
        this.apiKeyService = apiKeyService;
    }

    public ValidationResponse validate(String token, TokenTypeDto tokenType) {
        return switch (tokenType) {
            case JWT -> validateJwt(token);
            case API_KEY -> validateApiKey(token);
        };
    }

    private ValidationResponse validateJwt(String token) {
        try {
            Jwt jwt = jwtDecoder.decode(token);
            String subject = jwt.getSubject();
            Instant expiresAt = jwt.getExpiresAt();
            List<String> scopes = jwt.getClaimAsStringList("scope");
            return new ValidationResponse(true, subject, List.of(),
                    scopes != null ? scopes : List.of(), expiresAt);
        } catch (JwtException e) {
            log.debug("JWT validation failed: {}", e.getMessage());
            return ValidationResponse.invalid();
        }
    }

    private ValidationResponse validateApiKey(String rawKey) {
        try {
            var auth = apiKeyService.authenticate(rawKey);
            List<String> scopes = auth.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .map(a -> a.startsWith("SCOPE_") ? a.substring(6) : a)
                    .toList();
            return new ValidationResponse(true, auth.getName(), List.of(), scopes, null);
        } catch (Exception e) {
            log.debug("API key validation failed: {}", e.getMessage());
            return ValidationResponse.invalid();
        }
    }
}
