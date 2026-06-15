package dk.tinker.authservice.service;

import dk.tinker.authservice.event.TokenEventPublisher;
import dk.tinker.authservice.repository.ApiKeyRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Component
public class ApiKeyExpiryScheduler {

    private final ApiKeyRepository apiKeyRepository;
    private final TokenEventPublisher tokenEventPublisher;

    public ApiKeyExpiryScheduler(ApiKeyRepository apiKeyRepository,
            TokenEventPublisher tokenEventPublisher) {
        this.apiKeyRepository = apiKeyRepository;
        this.tokenEventPublisher = tokenEventPublisher;
    }

    @Scheduled(fixedDelay = 60_000)
    @Transactional(readOnly = true)
    public void publishExpiredKeyEvents() {
        Instant end = Instant.now();
        Instant start = end.minusSeconds(90);
        apiKeyRepository.findExpiredBetween(start, end)
                .forEach(key -> tokenEventPublisher.publishApiKeyRevoked(key.getKeyHash()));
    }
}
