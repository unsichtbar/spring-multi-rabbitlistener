package sh.ultima.multirabbitmq;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import com.rabbitmq.client.Channel;

import java.io.IOException;
import java.util.List;

import org.aopalliance.aop.Advice;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.config.RetryInterceptorBuilder;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.RabbitListenerContainerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@Configuration
@EnableConfigurationProperties(ProgrammaticQueuesProperties.class)
public class RabbitMqConfiguration {

    public static String EXCHANGE = "Events";
    public static String ERROR_EXCHANGE = "Errors";

    @Bean
    @Primary
    TopicExchange exchange() {
        return new TopicExchange(EXCHANGE);
    }

    @Bean(name = "errorExchange")
    TopicExchange errorExchange() {
        return new TopicExchange(ERROR_EXCHANGE);
    }

    @Bean
    RabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory connectionFactory, RabbitTemplate template) {
        var factory = new SimpleRabbitListenerContainerFactory();
        factory.setConcurrentConsumers(10);
        factory.setConnectionFactory(connectionFactory);
        factory.setDefaultRequeueRejected(false);
        factory.setAdviceChain(new Advice[]{ RetryInterceptorBuilder.stateless().maxAttempts(3).backOffOptions(500L, 2D, 5000L).build()});
        return factory;
    }

    @Bean
    Channel channel(final ConnectionFactory connectionFactory, final TopicExchange exchange, final TopicExchange errorExchange, List<Queue> queues, ProgrammaticQueuesProperties props) throws IOException {
        var connection = connectionFactory.createConnection();
        var channel = connection.createChannel(true);
        channel.exchangeDeclare(exchange.getName(), ExchangeTypes.TOPIC, true);
        
        for (Queue queue : queues) {
            try {
                channel.queueDeclare(queue.getName(), queue.isDurable(), queue.isExclusive(), queue.isAutoDelete(), queue.getArguments());
                props.queues.getOrDefault(queue.getName(), List.of()).iterator().forEachRemaining(eventCode -> {
                    try {
                        channel.queueBind(queue.getName()  , exchange.getName(), eventCode);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
            } catch(Exception e) {
                throw new RuntimeException(e);
            }
        }
        return channel;
    }
}
