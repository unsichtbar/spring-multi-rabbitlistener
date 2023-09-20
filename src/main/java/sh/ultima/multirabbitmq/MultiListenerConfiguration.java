package sh.ultima.multirabbitmq;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.GenericApplicationContext;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import static sh.ultima.multirabbitmq.RabbitMqConfiguration.ERROR_EXCHANGE;

import static sh.ultima.multirabbitmq.RabbitMqConfiguration.EXCHANGE;

@Configuration
@EnableConfigurationProperties(ProgrammaticQueuesProperties.class)
public class MultiListenerConfiguration {
    @Autowired
    private TopicExchange topicExchange;

    @Autowired
    private ProgrammaticQueuesProperties properties;

    
    @Autowired
    private ObjectMapper om;

    @Autowired
    private Logger logger;

    @Autowired
    private ApplicationContext ctx;

    @PostConstruct
    public void init() throws JsonProcessingException {
        // logger.info(om.writeValueAsString(properties));
        properties.queues.forEach((queue, events) -> {
            logger.info("add queue " + queue);
            Queue q = new Queue(queue, true, false, false);
            Queue errorQ = new Queue(queue + "Error", true, false, false);
            Map<String, Object> qProps = new HashMap<>();
            qProps.put("x-dead-letter-exchange", ERROR_EXCHANGE);
            
            qProps.put("x-dead-letter-routing-key", queue + "Error");
            ((GenericApplicationContext) ctx).registerBean(queue + "Queue", Queue.class, () -> q);

            ((GenericApplicationContext) ctx).registerBean(queue + "ErrorQueue", Queue.class, () -> errorQ);

            // events.forEach(event -> {
            //     BindingBuilder.bind(q).to(topicExchange).with(event);
            // });
        });

    }
}
