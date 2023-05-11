package amqpcore.utils;

import amqpcore.consumer.listener.Handler;
import amqpcore.consumer.listener.Listener;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import io.micrometer.tracing.SpanAndScope;
import io.micrometer.tracing.test.simple.SimpleSpan;
import io.micrometer.tracing.test.simple.SimpleSpanAndScope;
import io.micrometer.tracing.test.simple.SimpleSpanInScope;
import io.micrometer.tracing.test.simple.SimpleTracer;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.SneakyThrows;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.UUID;

@ExtendWith(MockitoExtension.class)
public abstract class AMQPTest {
    protected final EasyRandom easyRandom = new EasyRandom();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Getter
    @Setter
    @EqualsAndHashCode
    @NoArgsConstructor
    @AllArgsConstructor
    protected static class SomeDto {
        private String FirstField;
        private Integer SecondField;
        private Boolean ThirdField;
    }

    @NoArgsConstructor
    protected static class SomeListener implements Listener {
        public static final String route = "some_route";
        public static final SomeDto response = new SomeDto("FirstField", 3, true);


        @Handler(route = route)
        public SomeDto getSomeDtoById(UUID userId) {
            return response;
        }
    }

    protected Message getMessage(Object body) {
        return this.getMessage(SomeListener.route, body);
    }

    protected Message getMessage(String route, Object body) {
        return MessageBuilder
                .withBody(this.convertToBytes(body))
                .setMessageId(UUID.randomUUID().toString())
                .setCorrelationId(UUID.randomUUID().toString())
                .setContentType("application/json")
                .setContentEncoding("UTF-8")
                .setType(route)
                .build();
    }

    @SneakyThrows
    protected byte[] convertToBytes(Object obj) {
        return objectMapper.writeValueAsBytes(obj);
    }

    @SneakyThrows
    protected <T> T convertToObject(byte[] body, Class<T> clazz) {
        return objectMapper.readValue(body, clazz);
    }

    protected TypeFactory getTypeFactory() {
        return objectMapper.getTypeFactory();
    }

    protected SimpleSpan getSimpleSpan(SimpleTracer simpleTracer) {
        final var simpleSpan = new SimpleSpan().name("testSpanName");
        simpleSpan.start();
        simpleTracer.getSpans().add(simpleSpan);
        final var spanInScope = new SimpleSpanInScope(simpleSpan, new ThreadLocal<>());
        final var simpleSpanAndScope = new SimpleSpanAndScope(simpleSpan, spanInScope);
        final var thread = new ThreadLocal<SpanAndScope>();
        thread.set(simpleSpanAndScope);
        ReflectionTestUtils.setField(simpleTracer, "scopedSpans", thread);

        return simpleSpan;
    }
}