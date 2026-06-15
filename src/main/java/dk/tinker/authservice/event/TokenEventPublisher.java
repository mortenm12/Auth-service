package dk.tinker.authservice.event;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class TokenEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    public TokenEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publishApiKeyRevoked(String keyHash) {
        rabbitTemplate.convertAndSend(
                TokenEventConstants.EXCHANGE,
                TokenEventConstants.ROUTING_KEY_API_KEY_REVOKED,
                new ApiKeyRevokedEvent(keyHash)
        );
    }

    public void publishUserLogout(String userId) {
        rabbitTemplate.convertAndSend(
                TokenEventConstants.EXCHANGE,
                TokenEventConstants.ROUTING_KEY_SESSION_LOGOUT,
                new UserLogoutEvent(userId)
        );
    }
}
