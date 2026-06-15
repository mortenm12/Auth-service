package dk.tinker.authservice.security;

import dk.tinker.authservice.event.TokenEventPublisher;
import dk.tinker.authservice.repository.UserRepository;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessEvent;
import org.springframework.stereotype.Component;

@Component
public class LogoutEventListener {

    private final TokenEventPublisher tokenEventPublisher;
    private final UserRepository userRepository;

    public LogoutEventListener(TokenEventPublisher tokenEventPublisher,
            UserRepository userRepository) {
        this.tokenEventPublisher = tokenEventPublisher;
        this.userRepository = userRepository;
    }

    @EventListener
    public void onLogout(LogoutSuccessEvent event) {
        Authentication auth = event.getAuthentication();
        if (auth instanceof UsernamePasswordAuthenticationToken) {
            String username = auth.getName();
            userRepository.findByUsername(username).ifPresent(user ->
                    tokenEventPublisher.publishUserLogout(user.getId().toString())
            );
        }
    }
}
