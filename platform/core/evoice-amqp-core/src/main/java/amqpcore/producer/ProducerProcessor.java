package amqpcore.producer;

import amqpcore.exception.AMQPProducerException;
import amqpcore.exception.AMQPValidationException;
import amqpcore.utils.AMQPUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import io.micrometer.tracing.Tracer;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.MessageConversionException;
import org.springframework.validation.annotation.Validated;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@Validated
public class ProducerProcessor {
    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;
    private final Tracer tracer;
    private final ObservationRegistry observationRegistry;

    public ProducerProcessor(RabbitTemplate rabbitTemplate, ObjectMapper objectMapper, Tracer tracer, ObservationRegistry observationRegistry) {
        this.rabbitTemplate = rabbitTemplate;
        this.objectMapper = objectMapper;
        this.tracer = tracer;
        this.observationRegistry = observationRegistry;
    }

    public <T> Optional<T> sendAndReceive(@NotEmpty String consumer,
                                          @NotEmpty String route,
                                          @NotNull Object request,
                                          @NotNull Class<T> clazz) {
        final var response = this.trySendMessage(consumer, route, request);

        try {
            return Optional.ofNullable(objectMapper.readValue(response.getBody(), clazz));
        } catch (IOException e) {
            throw new MessageConversionException("Failed to convert message body", e);
        }
    }

    public <T> List<T> sendAndReceiveAsList(@NotEmpty String consumer,
                                            @NotEmpty String route,
                                            @NotNull Object request,
                                            @NotNull Class<T> clazz) {
        final var response = this.trySendMessage(consumer, route, request);

        try {
            return objectMapper.readValue(response.getBody(), objectMapper.getTypeFactory().constructCollectionType(List.class, clazz));
        } catch (IOException e) {
            throw new MessageConversionException("Failed to convert message body", e);
        }
    }

    private Message trySendMessage(@NotEmpty String consumer,
                                   @NotEmpty String route,
                                   @NotNull Object request) {
        if (Strings.isEmpty(AMQPUtils.REQIEST_QUEUE_SUFFIX)) {
            throw new AMQPValidationException("Failed to build request queue name: queue suffix is empty");
        }

        final var queueName = consumer + AMQPUtils.REQIEST_QUEUE_SUFFIX;
        final var messageId = UUID.randomUUID().toString();

        log.info(String.format(
                "Attempt to send a message: {%s} to the queue: '%s', messageId: '%s'",
                request,
                queueName,
                messageId
        ));

        AtomicReference<Message> response = new AtomicReference<>();
        try {
            final var message = MessageBuilder
                    .withBody(objectMapper.writeValueAsBytes(request))
                    .setMessageId(messageId)
                    .setCorrelationId(UUID.randomUUID().toString())
                    .setContentType("application/json")
                    .setContentEncoding("UTF-8")
                    .setType(route)
                    .build();

            log.info("Send message into " + queueName + ", " + message);

            Observation.createNotStarted(route, this.observationRegistry).observe(() -> {
                log.info("ZIPKIN TRACE:{}", this.tracer.currentSpan().context().traceId());
                response.set(rabbitTemplate.sendAndReceive("", queueName, message));
            });
        } catch (Exception e) {
            throw new AMQPProducerException(e.getMessage(), e);
        }

        if (response.get() == null) {
            throw new AMQPProducerException(String.format(
                    "Couldn't get response from consumer: %s by route: %s",
                    consumer,
                    route
            ));
        }

        return response.get();
    }
}
