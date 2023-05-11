package amqpcore.consumer;

import amqpcore.consumer.listener.Handler;
import amqpcore.consumer.listener.Listener;
import amqpcore.exception.AMQPProducerException;
import amqpcore.exception.AMQPValidationException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import io.micrometer.tracing.Tracer;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.support.converter.MessageConversionException;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@Validated
public class ConsumerProcessor {
    private static final String DEFAULT_CHARSET = "UTF-8";

    private final ObjectMapper objectMapper;
    private final Map<String, MethodInvocation> handlers;
    private final Tracer tracer;
    private final ObservationRegistry observationRegistry;

    @Getter
    @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
    public static class MethodInvocation {
        Object instance;
        Method method;
        Class<?> clazz;

        public MethodInvocation(Object instance, Method method) {
            this.instance = instance;
            this.method = method;
            this.clazz = method.getParameterTypes()[0];
        }

        @SneakyThrows
        public Object invoke(Object... args) {
            return this.method.invoke(instance, args);
        }
    }

    public ConsumerProcessor(Set<Listener> listeners, ObjectMapper objectMapper, Tracer tracer, ObservationRegistry observationRegistry) {
        this.objectMapper = objectMapper;
        this.handlers = new HashMap<>();
        this.tracer = tracer;
        this.observationRegistry = observationRegistry;

        for (Listener listener : listeners) {
            for (Method declaredMethod : listener.getClass().getDeclaredMethods()) {
                if (declaredMethod.isAnnotationPresent(Handler.class)) {
                    this.handlers.put(
                            declaredMethod.getAnnotation(Handler.class).route(),
                            new MethodInvocation(listener, declaredMethod)
                    );
                }
            }
        }
    }

    public Object process(Message message) {
        final var messageType = message.getMessageProperties().getType();
        if (CollectionUtils.isEmpty(handlers) || !handlers.containsKey(messageType)) {
            throw new AMQPValidationException(String.format(
                    "Failed to receiving handler: handler for messageType '%s' is not declared",
                    messageType
            ));
        }

        log.info(String.format(
                "Attempt to receive a message: {%s} to the queue: '%s', messageId: '%s'",
                message,
                message.getMessageProperties().getConsumerQueue(),
                message.getMessageProperties().getMessageId()
        ));

        final var handler = handlers.get(messageType);
        final var resolveObject = this.readBody(message, handler.getClazz(), objectMapper);
        try {
            log.info(String.format(
                    "Consume message by handler: {%s}",
                    handler.instance.getClass()
            ));

            AtomicReference<Object> response = new AtomicReference<>();
            Observation.createNotStarted(messageType, this.observationRegistry).observe(() -> {
                log.info("ZIPKIN TRACE:{}", this.tracer.currentSpan().context().traceId());
                response.set(handler.invoke(resolveObject));
            });

            return response.get();
        } catch (Exception e) {
            throw new AMQPProducerException("Failed to receiving message", e);
        }
    }

    private Object readBody(Message message, Class<?> targetDtoClass, ObjectMapper objectMapper) {
        final var contentType = message.getMessageProperties().getContentType();
        if (contentType != null && !contentType.contains(MessageProperties.CONTENT_TYPE_JSON)) {
            throw new MessageConversionException("Failed to convert Message with content-type " + contentType);
        }

        String contentAsString;
        try {
            final var encoding = message.getMessageProperties().getContentEncoding() != null
                                 ? message.getMessageProperties().getContentEncoding() : DEFAULT_CHARSET;
            contentAsString = new String(message.getBody(), encoding);
        } catch (Exception ex) {
            throw new MessageConversionException("Failed to convert message body", ex);
        }

        try {
            return objectMapper.readValue(contentAsString, targetDtoClass);
        } catch (IOException ioException) {
            throw new MessageConversionException("Failed to read json to target class.", ioException);
        }
    }
}
