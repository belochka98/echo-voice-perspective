package amqpcore.consumer;

import amqpcore.utils.AMQPTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.core.Message;

@ExtendWith(MockitoExtension.class)
class ConsumerImplTest extends AMQPTest {
    @InjectMocks
    private ConsumerImpl consumer;
    @Mock
    private ConsumerProcessor consumerProcessor;

    @Test
    @DisplayName("Test message processing (POSITIVE)")
    void testProcessMessage() {
        final var someDto = easyRandom.nextObject(SomeDto.class);
        Mockito.doReturn(someDto).when(consumerProcessor).process(ArgumentMatchers.any(Message.class));

        final var message = this.getMessage(someDto);
        final var result = (SomeDto) consumer.processMessage(message);
        Assertions.assertEquals(someDto, result);
    }
}