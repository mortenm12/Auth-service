package dk.tinker.authservice.event;

/**
 * Payload sent to permission-lib clients via SSE when a token is invalidated.
 */
public record TokenInvalidationEvent(
        String type,
        String token,
        String subject,
        String keyHash
) {
    public static TokenInvalidationEvent subjectInvalidated(String subject) {
        return new TokenInvalidationEvent("SUBJECT_INVALIDATED", null, subject, null);
    }

    public static TokenInvalidationEvent apiKeyInvalidated(String keyHash) {
        return new TokenInvalidationEvent("API_KEY_INVALIDATED", null, null, keyHash);
    }
}
