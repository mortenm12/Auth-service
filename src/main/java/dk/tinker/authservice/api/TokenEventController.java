package dk.tinker.authservice.api;

/**
 * Token invalidation events are now delivered via RabbitMQ (exchange: token.invalidation).
 * Microservices using permission-lib receive events automatically through their AMQP listener.
 */
final class TokenEventController {
    private TokenEventController() {}
}
