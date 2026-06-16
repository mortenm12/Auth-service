package dk.tinker.authservice.config;

import org.springframework.amqp.core.FanoutExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfig {

    public static final String TOKEN_INVALIDATION_EXCHANGE = "token.invalidation";

    @Bean
    public FanoutExchange tokenInvalidationExchange() {
        return new FanoutExchange(TOKEN_INVALIDATION_EXCHANGE, true, false);
    }
}
