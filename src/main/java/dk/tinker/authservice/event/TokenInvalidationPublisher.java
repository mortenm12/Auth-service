package dk.tinker.authservice.event;

import com.fasterxml.jackson.databind.ObjectMapper;
import dk.tinker.authservice.config.RabbitMqConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
public class TokenInvalidationPublisher {

    private static final Logger LOG = LoggerFactory.getLogger(TokenInvalidationPublisher.class);

    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    public TokenInvalidationPublisher(RabbitTemplate rabbitTemplate, ObjectMapper objectMapper) {
        this.rabbitTemplate = rabbitTemplate;
        this.objectMapper = objectMapper;
    }

    public void publishSubjectInvalidated(String subject) {
        publish(TokenInvalidationEvent.subjectInvalidated(subject));
    }

    public void publishApiKeyInvalidated(String keyHash) {
        publish(TokenInvalidationEvent.apiKeyInvalidated(keyHash));
    }

    private void publish(TokenInvalidationEvent event) {
        try {
            String json = objectMapper.writeValueAsString(event);
            rabbitTemplate.convertAndSend(RabbitMqConfig.TOKEN_INVALIDATION_EXCHANGE, "", json);
            LOG.debug("Published invalidation event: {}", event.type());
        } catch (Exception e) {
            LOG.error("Failed to publish token invalidation event", e);
        }
    }
}
