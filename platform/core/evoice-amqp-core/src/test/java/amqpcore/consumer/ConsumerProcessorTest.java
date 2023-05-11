package amqpcore.consumer;

import amqpcore.consumer.listener.Handler;
import amqpcore.exception.AMQPProducerException;
import amqpcore.exception.AMQPValidationException;
import amqpcore.utils.AMQPTest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.observation.tck.TestObservationRegistry;
import io.micrometer.tracing.test.simple.SimpleTracer;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.amqp.support.converter.MessageConversionException;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@ExtendWith(SpringExtension.class)
class ConsumerProcessorTest extends AMQPTest {
    @Mock
    private ObjectMapper objectMapper;

    private SomeListener someListener;
    private SimpleTracer simpleTracer;
    private ConsumerProcessor consumerProcessor;

    @BeforeEach
    void setUp() {
        this.someListener = new SomeListener();
        this.simpleTracer = new SimpleTracer();
        this.consumerProcessor = new ConsumerProcessor(Set.of(someListener), objectMapper, simpleTracer, TestObservationRegistry.create());
    }

    @SuppressWarnings("unchecked")
    @Test
    @DisplayName("Test class constructor (POSITIVE)")
    void testConsumerProcessor() {
        final var route = someListener.getClass().getMethods()[0].getAnnotation(Handler.class).route();
        final var handlers = (Map<String, ConsumerProcessor.MethodInvocation>) ReflectionTestUtils.getField(consumerProcessor, "handlers");

        Assertions.assertNotNull(handlers);
        Assertions.assertTrue(handlers.containsKey(route));
        Assertions.assertEquals(someListener, handlers.get(route).getInstance());
    }

    @SneakyThrows
    @Test
    @DisplayName("Test body reading (POSITIVE)")
    void testReadBody() {
        final var someDto = easyRandom.nextObject(SomeDto.class);
        final var message = this.getMessage(someDto);
        Mockito.doReturn(someDto).when(objectMapper).readValue(ArgumentMatchers.anyString(), ArgumentMatchers.eq(SomeDto.class));

        final var result = (SomeDto) ReflectionTestUtils.invokeMethod(consumerProcessor, "readBody", message, SomeDto.class, objectMapper);
        Assertions.assertEquals(someDto, result);
    }

    @SneakyThrows
    @Test
    @DisplayName("Test content type error while trying to read body (NEGATIVE)")
    void testReadBodyContentTypeError() {
        final var someDto = easyRandom.nextObject(SomeDto.class);
        final var message = this.getMessage(someDto);
        message.getMessageProperties().setContentType("text/javascript");

        Class<? extends Exception> resultException = null;
        try {
            ReflectionTestUtils.invokeMethod(consumerProcessor, "readBody", message, SomeDto.class, objectMapper);
        } catch (Exception e) {
            resultException = e.getClass();
        }

        Assertions.assertEquals(resultException, MessageConversionException.class);
    }

    @SneakyThrows
    @Test
    @DisplayName("Test error while trying to recognize message body (NEGATIVE)")
    void testReadBodyObjectMapperError() {
        final var someDto = easyRandom.nextObject(SomeDto.class);
        final var message = this.getMessage(someDto);
        Mockito.doThrow(JsonProcessingException.class).when(objectMapper).readValue(ArgumentMatchers.anyString(), ArgumentMatchers.eq(SomeDto.class));

        Class<? extends Exception> resultException = null;
        try {
            ReflectionTestUtils.invokeMethod(consumerProcessor, "readBody", message, SomeDto.class, objectMapper);
        } catch (Exception e) {
            resultException = e.getClass();
        }

        Assertions.assertEquals(resultException, MessageConversionException.class);
    }

    @SneakyThrows
    @Test
    @DisplayName("Test processing (POSITIVE)")
    void testProcess() {
        final var someId = UUID.randomUUID();
        final var message = this.getMessage(someId);
        Mockito.doReturn(someId).when(objectMapper).readValue(ArgumentMatchers.anyString(), ArgumentMatchers.eq(UUID.class));

        final var simpleSpan = this.getSimpleSpan(simpleTracer);
        Assertions.assertEquals(SomeListener.response, consumerProcessor.process(message));
        simpleSpan.end();

        Assertions.assertEquals(simpleTracer.onlySpan().getName(), "testSpanName");
    }

    @Test
    @DisplayName("Test error while message processing with empty handlers set (NEGATIVE)")
    void testProcessHandlersSetIsEmpty() {
        final var someId = UUID.randomUUID();
        final var message = this.getMessage(someId);
        ReflectionTestUtils.setField(consumerProcessor, "handlers", Collections.emptyMap());

        Assertions.assertThrows(AMQPValidationException.class, () -> consumerProcessor.process(message));
    }

    @SneakyThrows
    @Test
    @DisplayName("Test error while message processing with invalid body class (NEGATIVE)")
    void testProcessInvalidBodyClass() {
        final var someDto = easyRandom.nextObject(SomeDto.class);
        final var message = this.getMessage(someDto);
        Mockito.doReturn(someDto).when(objectMapper).readValue(ArgumentMatchers.anyString(), ArgumentMatchers.eq(UUID.class));

        Assertions.assertThrows(AMQPProducerException.class, () -> consumerProcessor.process(message));
    }
}