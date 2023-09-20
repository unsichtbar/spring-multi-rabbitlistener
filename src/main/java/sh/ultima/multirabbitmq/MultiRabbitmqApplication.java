package sh.ultima.multirabbitmq;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class MultiRabbitmqApplication {

	public static void main(String[] args) {
		SpringApplication.run(MultiRabbitmqApplication.class, args);
	}


	@Bean
	public Logger logger() {
		return LoggerFactory.getLogger(MultiRabbitmqApplication.class);
	}

}
