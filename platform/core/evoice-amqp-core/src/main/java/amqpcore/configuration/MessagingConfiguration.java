package amqpcore.configuration;

import amqpcore.consumer.Consumer;
import amqpcore.consumer.ConsumerImpl;
import amqpcore.consumer.ConsumerProcessor;
import amqpcore.consumer.listener.Listener;
import amqpcore.producer.ProducerProcessor;
import amqpcore.utils.AMQPUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.micrometer.observation.ObservationRegistry;
import io.micrometer.tracing.Tracer;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.rabbit.config.ContainerCustomizer;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;
import java.util.Objects;
import java.util.Set;

@Configuration
public class MessagingConfiguration {
    @Value("${spring.application.name}" + AMQPUtils.REQIEST_QUEUE_SUFFIX)
    private String queueName;

    @Value("${spring.application.name}" + AMQPUtils.REQIEST_DLQ_QUEUE_SUFFIX)
    private String dlqName;

    @Bean
    @ConditionalOnMissingBean
    ObjectMapper getObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return objectMapper;
    }

    @Bean
    @ConditionalOnMissingBean
    public MessageConverter jackson2MessageConverter(ObjectMapper objectMapper) {
        Jackson2JsonMessageConverter converter = new Jackson2JsonMessageConverter(objectMapper);
        return converter;
    }

    @Bean
    public Queue asyncMessagingRequestQueue() {
        return QueueBuilder.durable(queueName)
                .withArgument("x-dead-letter-exchange", "")
                .withArgument("x-dead-letter-routing-key", dlqName)
                .build();
    }

    @Bean
    Queue asyncMessagingRequestDeadLetterQueue() {
        return QueueBuilder.durable(dlqName).build();
    }

    @Bean
    public ConsumerProcessor consumerProcessor(Set<Listener> listeners, ObjectMapper objectMapper, Tracer tracer, ObservationRegistry observationRegistry) {
        return new ConsumerProcessor(
                Objects.requireNonNullElse(listeners, Collections.emptySet()),
                objectMapper,
                tracer,
                observationRegistry
        );
    }

    @Bean
    ContainerCustomizer<SimpleMessageListenerContainer> containerCustomizer() {
        return (container) -> container.setObservationEnabled(true);
    }

    @Bean
    public Consumer consumer(ConsumerProcessor consumerProcessor) {
        return new ConsumerImpl(consumerProcessor);
    }

    @Bean
    public ProducerProcessor producerProcessor(RabbitTemplate rabbitTemplate, ObjectMapper objectMapper, Tracer tracer, ObservationRegistry observationRegistry) {
        rabbitTemplate.setObservationEnabled(true);

        return new ProducerProcessor(
                rabbitTemplate,
                objectMapper,
                tracer,
                observationRegistry
        );
    }
}
