package sh.ultima.multirabbitmq;

import java.util.List;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "smart")
public class ProgrammaticQueuesProperties {

    Map<String, List<String>> queues;

    public ProgrammaticQueuesProperties() {
    }

    public Map<String, List<String>> getQueues() {
        return queues;
    }

    public void setQueues(Map<String, List<String>> queues) {
        this.queues = queues;
    }


  

    
    
}
