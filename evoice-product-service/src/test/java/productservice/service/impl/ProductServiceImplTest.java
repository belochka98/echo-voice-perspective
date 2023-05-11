package productservice.service.impl;

import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.util.CollectionUtils;
import productservice.entity.Product;
import productservice.repository.ProductRepository;

import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {
    @InjectMocks
    private ProductServiceImpl productService;
    @Mock
    private ProductRepository productRepository;

    private final EasyRandom easyRandom = new EasyRandom();

    @Test
    @DisplayName("Test getting user product by userId (POSITIVE / NEGATIVE)")
    void testGetProductsByUserId() {
        final var userId = UUID.randomUUID();
        final var products = easyRandom.objects(Product.class, 3).toList();
        Mockito.doReturn(products).when(productRepository).findAllByUsersContains(userId);

        Assertions.assertEquals(products, productService.getProductsByUserId(userId));
        Assertions.assertTrue(CollectionUtils.isEmpty(productService.getProductsByUserId(UUID.randomUUID())));
    }
}