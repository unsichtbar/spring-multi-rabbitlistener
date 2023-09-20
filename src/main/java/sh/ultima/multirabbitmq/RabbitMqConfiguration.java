package sh.ultima.multirabbitmq;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.amqp.core.TopicExchange;

@Configuration
public class RabbitMqConfiguration {

    public static String EXCHANGE = "Events";
    public static String ERROR_EXCHANGE = "Errors";

    @Bean
    TopicExchange exchange() {
        return new TopicExchange(EXCHANGE);
    }

}
