package dk.tinker.authservice.api;

import dk.tinker.authservice.event.TokenInvalidationPublisher;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/api/v1/token-events")
@Tag(name = "Token Events", description = "SSE stream of token invalidation events for microservices")
public class TokenEventController {

    private final TokenInvalidationPublisher publisher;

    public TokenEventController(TokenInvalidationPublisher publisher) {
        this.publisher = publisher;
    }

    @GetMapping(produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @Operation(summary = "Subscribe to token invalidation events via SSE")
    public SseEmitter subscribe() {
        return publisher.subscribe();
    }
}
