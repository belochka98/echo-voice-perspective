package amqpcore.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.amqp.core.Message;

public interface Consumer {
    Object processMessage(Message message) throws JsonProcessingException;
}
