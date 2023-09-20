package sh.ultima.multirabbitmq;

import org.slf4j.Logger;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class FooListener {

    @Autowired
    private Logger logger;
    
    @RabbitListener(queues = "queue1")
    public void listen(Message message) {
        logger.info("FooListener received message.");
       // throw new RuntimeException("business logic failure");
    }
}
