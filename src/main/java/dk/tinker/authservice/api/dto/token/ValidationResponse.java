package dk.tinker.authservice.api.dto.token;

import java.time.Instant;
import java.util.List;

public record ValidationResponse(
        boolean valid,
        String subject,
        List<String> roles,
        List<String> scopes,
        Instant expiresAt
) {
    public static ValidationResponse invalid() {
        return new ValidationResponse(false, null, List.of(), List.of(), null);
    }
}
