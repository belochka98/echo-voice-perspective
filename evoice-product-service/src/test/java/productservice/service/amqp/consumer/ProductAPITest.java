package productservice.service.amqp.consumer;

import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.CollectionUtils;
import productservice.entity.Product;
import productservice.entity.ProductName;
import productservice.entity.ProductPrice;
import productservice.mapper.ProductMapper;
import productservice.service.ProductService;

import java.util.List;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class ProductAPITest {
    @InjectMocks
    private ProductAPI productAPI;
    @Mock
    private ProductService productService;

    private final ProductMapper productMapper = Mappers.getMapper(ProductMapper.class);
    private final EasyRandom easyRandom = new EasyRandom();

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(productAPI, "productMapper", productMapper);
    }

    @Test
    @DisplayName("Test getting user product by userId (POSITIVE / NEGATIVE)")
    void testGetProductsByUserId() {
        final var userId = UUID.randomUUID();
        final var products = easyRandom.objects(Product.class, 3).peek(p -> {
            final var actualName = easyRandom.nextObject(ProductName.class);
            actualName.setEnd_date(null);
            p.setNames(List.of(actualName));

            final var actualPrice = easyRandom.nextObject(ProductPrice.class);
            actualPrice.setEnd_date(null);
            p.setPrices(List.of(actualPrice));
        }).toList();
        Mockito.doReturn(products).when(productService).getProductsByUserId(userId);

        Assertions.assertEquals(productMapper.applyAsListDto(products), productAPI.getProductsByUserId(userId));
        Assertions.assertTrue(CollectionUtils.isEmpty(productService.getProductsByUserId(UUID.randomUUID())));
    }
}