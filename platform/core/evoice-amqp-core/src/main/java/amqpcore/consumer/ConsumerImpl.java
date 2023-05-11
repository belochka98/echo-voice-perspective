package amqpcore.consumer;

import amqpcore.utils.AMQPUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;

@Slf4j
@RequiredArgsConstructor
public class ConsumerImpl implements Consumer {
    private final ConsumerProcessor consumerProcessor;

    @RabbitListener(queues = {"${spring.application.name}" + AMQPUtils.REQIEST_QUEUE_SUFFIX}, concurrency = "1-50")
    public Object processMessage(Message message) {
        log.debug(String.format(
                "Request received messageId: %s, correlationId: %s",
                message.getMessageProperties().getMessageId(),
                message.getMessageProperties().getCorrelationId()
        ));

        return consumerProcessor.process(message);
    }
}
