package amqpcore.producer;

import amqpcore.exception.AMQPProducerException;
import amqpcore.utils.AMQPTest;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import io.micrometer.observation.tck.TestObservationRegistry;
import io.micrometer.tracing.test.simple.SimpleTracer;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.MessageConversionException;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class ProducerProcessorTest extends AMQPTest {
    @Mock
    private ObjectMapper objectMapper;
    @Mock
    private RabbitTemplate rabbitTemplate;

    private SimpleTracer simpleTracer;
    private ProducerProcessor producerProcessor;

    @BeforeEach
    void setUp() {
        this.simpleTracer = new SimpleTracer();
        this.producerProcessor = new ProducerProcessor(rabbitTemplate, objectMapper, simpleTracer, TestObservationRegistry.create());
    }

    @SneakyThrows
    @ParameterizedTest(name = "POSITIVE: ''{0}''")
    @CsvSource({"true", "false"})
    @DisplayName("Test message sending")
    void testSendAndReceive(boolean isPositive) {
        final var consumer = easyRandom.nextObject(String.class);
        final var route = easyRandom.nextObject(String.class);
        final var request = UUID.randomUUID();
        final var clazz = SomeDto.class;
        final var someDto = easyRandom.nextObject(SomeDto.class);
        final var response = this.getMessage(someDto);

        Mockito.doReturn(this.convertToBytes(request)).when(objectMapper).writeValueAsBytes(request);
        if (isPositive) {
            Mockito.doReturn(someDto).when(objectMapper).readValue(this.convertToBytes(someDto), clazz);
        } else {
            Mockito.doThrow(IOException.class).when(objectMapper).readValue(this.convertToBytes(someDto), clazz);
        }

        Mockito.doReturn(response).when(rabbitTemplate).sendAndReceive(
                ArgumentMatchers.anyString(),
                ArgumentMatchers.anyString(),
                ArgumentMatchers.any(Message.class)
        );

        final var simpleSpan = this.getSimpleSpan(simpleTracer);
        if (isPositive) {
            Assertions.assertEquals(someDto, producerProcessor.sendAndReceive(consumer, route, request, clazz).orElseThrow());
        } else {
            Assertions.assertThrows(
                    MessageConversionException.class,
                    () -> producerProcessor.sendAndReceive(consumer, route, request, clazz).orElseThrow()
            );
        }
        simpleSpan.end();
    }

    @SneakyThrows
    @ParameterizedTest(name = "POSITIVE: ''{0}''")
    @CsvSource({"true", "false"})
    @DisplayName("Test message sending as list")
    void testSendAndReceiveAsList(boolean isPositive) {
        final var consumer = easyRandom.nextObject(String.class);
        final var route = easyRandom.nextObject(String.class);
        final var request = UUID.randomUUID();
        final var clazz = SomeDto.class;
        final var someDto = easyRandom.nextObject(SomeDto.class);
        final var response = this.getMessage(someDto);

        Mockito.doReturn(this.convertToBytes(request)).when(objectMapper).writeValueAsBytes(request);
        if (isPositive) {
            Mockito.doReturn(this.getTypeFactory()).when(objectMapper).getTypeFactory();
            Mockito.doReturn(List.of(someDto)).when(objectMapper).readValue(
                    ArgumentMatchers.eq(this.convertToBytes(someDto)),
                    ArgumentMatchers.any(CollectionType.class)
            );
        } else {
            Mockito.doReturn(this.getTypeFactory()).when(objectMapper).getTypeFactory();
            Mockito.doThrow(IOException.class).when(objectMapper).readValue(
                    ArgumentMatchers.eq(this.convertToBytes(someDto)),
                    ArgumentMatchers.any(CollectionType.class)
            );
        }

        Mockito.doReturn(response).when(rabbitTemplate).sendAndReceive(
                ArgumentMatchers.anyString(),
                ArgumentMatchers.anyString(),
                ArgumentMatchers.any(Message.class)
        );

        final var simpleSpan = this.getSimpleSpan(simpleTracer);
        if (isPositive) {
            Assertions.assertEquals(List.of(someDto), producerProcessor.sendAndReceiveAsList(consumer, route, request, clazz));
        } else {
            Assertions.assertThrows(
                    MessageConversionException.class,
                    () -> producerProcessor.sendAndReceiveAsList(consumer, route, request, clazz)
            );
        }
        simpleSpan.end();
    }

    @SuppressWarnings("ConstantConditions")
    @SneakyThrows
    @ParameterizedTest(name = "POSITIVE: ''{0}''")
    @CsvSource({"true", "false"})
    @DisplayName("Test trying to send message")
    void testTrySendMessage(boolean isPositive) {
        final var consumer = easyRandom.nextObject(String.class);
        final var route = easyRandom.nextObject(String.class);
        final var request = UUID.randomUUID();
        final var someDto = easyRandom.nextObject(SomeDto.class);
        final var response = this.getMessage(someDto);

        Mockito.doReturn(this.convertToBytes(request)).when(objectMapper).writeValueAsBytes(request);

        if (isPositive) {
            Mockito.doReturn(response).when(rabbitTemplate).sendAndReceive(
                    ArgumentMatchers.anyString(),
                    ArgumentMatchers.anyString(),
                    ArgumentMatchers.any(Message.class)
            );
        } else {
            Mockito.doThrow(new NullPointerException("someException")).when(rabbitTemplate).sendAndReceive(
                    ArgumentMatchers.anyString(),
                    ArgumentMatchers.anyString(),
                    ArgumentMatchers.any(Message.class)
            );
        }

        final var simpleSpan = this.getSimpleSpan(simpleTracer);
        if (isPositive) {
            final var result = (Message) ReflectionTestUtils.invokeMethod(producerProcessor, "trySendMessage", consumer, route, request);

            Assertions.assertEquals(someDto, this.convertToObject(result.getBody(), SomeDto.class));
        } else {
            Class<? extends Exception> resultException = null;
            try {
                ReflectionTestUtils.invokeMethod(producerProcessor, "trySendMessage", consumer, route, request);
            } catch (Exception e) {
                resultException = e.getClass();
            }

            Assertions.assertEquals(AMQPProducerException.class, resultException);
        }
        simpleSpan.end();
    }
}