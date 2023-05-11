package productservice.repository;

import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import productservice.entity.Product;
import productservice.entity.ProductType;

import java.util.UUID;

@ActiveProfiles("test")
@DataJpaTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ProductRepositoryTest {
    @Autowired
    private ProductRepository repository;

    @Autowired
    private ProductTypeRepository productTypeRepository;

    private final EasyRandom easyRandom = new EasyRandom();

    @Test
    @DisplayName("Test for receiving products by userId (POSITIVE)")
    void testFindAllByUsersContains() {
        final var userIds = easyRandom.objects(UUID.class, 3).toList();
        final var productType = productTypeRepository.saveAndFlush(easyRandom.nextObject(ProductType.class));

        final var products = easyRandom.objects(Product.class, 3).peek(p -> {
            p.setType(productType);
            p.setUsers(userIds);
            p.getPrices().clear();
            p.getNames().clear();
        }).toList();

        Assertions.assertDoesNotThrow(() -> repository.saveAllAndFlush(products));
        org.assertj.core.api.Assertions.assertThat(repository.findAllByUsersContains(userIds.stream().findAny().orElseThrow()))
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .ignoringFields("id")
                .isEqualTo(products);
    }
}