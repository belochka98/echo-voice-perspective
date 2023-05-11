package productservice.service.amqp.consumer;

import amqpcore.consumer.listener.Handler;
import apicore.amqp.RouteDefinition;
import apicore.amqp.product.ProductDto;
import apicore.amqp.product.ProductListener;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import productservice.mapper.ProductMapper;
import productservice.service.ProductService;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductAPI implements ProductListener {
    private final ProductMapper productMapper;
    private final ProductService productService;

    @Override
    @Handler(route = RouteDefinition.GET_PRODUCTS_BY_ID)
    public List<ProductDto> getProductsByUserId(UUID userId) {
        log.info("Start operation of receiving products for user");

        return productMapper.applyAsListDto(productService.getProductsByUserId(userId));
    }
}
