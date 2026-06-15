package dk.tinker.authservice.event;

public final class TokenEventConstants {

    public static final String EXCHANGE = "auth.events";
    public static final String ROUTING_KEY_API_KEY_REVOKED = "token.api-key.revoked";
    public static final String ROUTING_KEY_SESSION_LOGOUT = "token.session.logout";

    private TokenEventConstants() { }
}
