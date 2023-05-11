package apicore.amqp.product;

import amqpcore.producer.Producer;
import amqpcore.producer.ProducerProcessor;
import amqpcore.utils.AMQPUtils;
import apicore.amqp.RouteDefinition;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ProductProducer implements Producer {
    private final ProducerProcessor producerProcessor;

    public List<ProductDto> getProductsByUserId(UUID userId) {
        return producerProcessor.sendAndReceiveAsList(
                AMQPUtils.EVOICE_PRODUCT_SERVICE,
                RouteDefinition.GET_PRODUCTS_BY_ID,
                userId,
                ProductDto.class
        );
    }
}
