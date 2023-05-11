package apicore.amqp.product;

import amqpcore.consumer.listener.Listener;

import java.util.List;
import java.util.UUID;

public interface ProductListener extends Listener {
    List<ProductDto> getProductsByUserId(UUID userId);
}
