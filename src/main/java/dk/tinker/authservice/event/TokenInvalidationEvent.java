package dk.tinker.authservice.event;

/**
 * Payload sent to permission-lib clients via SSE when a token is invalidated.
 */
public record TokenInvalidationEvent(
        String type,
        String token,
        String subject,
        String keyHash,
        String resourceType,
        String resourceId
) {
    public static TokenInvalidationEvent subjectInvalidated(String subject) {
        return new TokenInvalidationEvent("SUBJECT_INVALIDATED", null, subject, null, null, null);
    }

    public static TokenInvalidationEvent apiKeyInvalidated(String keyHash) {
        return new TokenInvalidationEvent("API_KEY_INVALIDATED", null, null, keyHash, null, null);
    }

    public static TokenInvalidationEvent resourcePermissionChanged(String subject, String resourceType, String resourceId) {
        return new TokenInvalidationEvent("RESOURCE_PERMISSION_CHANGED", null, subject, null, resourceType, resourceId);
    }
}
