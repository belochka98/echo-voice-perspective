package apicore.amqp.product;

import amqpcore.producer.ProducerProcessor;
import amqpcore.utils.AMQPUtils;
import apicore.amqp.RouteDefinition;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class ProductProducerTest {
    @InjectMocks
    private ProductProducer productProducer;
    @Mock
    private ProducerProcessor producerProcessor;

    private final EasyRandom easyRandom = new EasyRandom();

    @Test
    @DisplayName("Test receiving products by userId (POSITIVE)")
    void testConsumerProcessor() {
        final var userId = UUID.randomUUID();
        final var response = easyRandom.objects(ProductDto.class, 3).toList();
        Mockito.doReturn(response).when(producerProcessor).sendAndReceiveAsList(
                AMQPUtils.EVOICE_PRODUCT_SERVICE,
                RouteDefinition.GET_PRODUCTS_BY_ID,
                userId,
                ProductDto.class
        );

        Assertions.assertEquals(response, productProducer.getProductsByUserId(userId));
    }
}