package userservice.service.amqp.producer;

import apicore.amqp.product.ProductDto;
import apicore.amqp.product.ProductProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductAPI {
    private final ProductProducer productProducer;

    public List<ProductDto> getUserProducts(UUID userId) {
        return productProducer.getProductsByUserId(userId);
    }
}
