package dk.tinker.authservice.security;

import dk.tinker.authservice.event.TokenInvalidationPublisher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;

import java.io.IOException;

/**
 * Publishes a SUBJECT_INVALIDATED event when a user logs out so that
 * all microservices using permission-lib evict that user's cached tokens.
 */
public class LogoutTokenInvalidationHandler extends SimpleUrlLogoutSuccessHandler {

    private final TokenInvalidationPublisher publisher;

    public LogoutTokenInvalidationHandler(TokenInvalidationPublisher publisher) {
        this.publisher = publisher;
        setDefaultTargetUrl("/");
    }

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {
        if (authentication != null) {
            publisher.publishSubjectInvalidated(authentication.getName());
        }
        super.onLogoutSuccess(request, response, authentication);
    }
}
