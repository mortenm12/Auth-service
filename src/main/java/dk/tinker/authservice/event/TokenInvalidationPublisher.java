package dk.tinker.authservice.event;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Manages active SSE connections from microservices and broadcasts
 * token invalidation events to all connected clients.
 */
@Service
public class TokenInvalidationPublisher {

    private static final Logger log = LoggerFactory.getLogger(TokenInvalidationPublisher.class);

    private final CopyOnWriteArrayList<SseEmitter> emitters = new CopyOnWriteArrayList<>();
    private final ObjectMapper objectMapper;

    public TokenInvalidationPublisher(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public SseEmitter subscribe() {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        emitters.add(emitter);
        emitter.onCompletion(() -> emitters.remove(emitter));
        emitter.onTimeout(() -> emitters.remove(emitter));
        emitter.onError(e -> emitters.remove(emitter));
        log.debug("New SSE subscriber added, total: {}", emitters.size());
        return emitter;
    }

    public void publish(TokenInvalidationEvent event) {
        String json;
        try {
            json = objectMapper.writeValueAsString(event);
        } catch (IOException e) {
            log.error("Failed to serialize invalidation event", e);
            return;
        }

        emitters.forEach(emitter -> {
            try {
                emitter.send(SseEmitter.event().data(json));
            } catch (IOException e) {
                emitter.completeWithError(e);
                emitters.remove(emitter);
            }
        });
    }

    public void publishSubjectInvalidated(String subject) {
        log.info("Publishing SUBJECT_INVALIDATED for subject: {}", subject);
        publish(TokenInvalidationEvent.subjectInvalidated(subject));
    }

    public void publishApiKeyInvalidated(String keyHash) {
        log.info("Publishing API_KEY_INVALIDATED for key hash prefix: {}", keyHash.substring(0, 8));
        publish(TokenInvalidationEvent.apiKeyInvalidated(keyHash));
    }
}
