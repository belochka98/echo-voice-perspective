package productservice.service.impl;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import productservice.entity.Product;
import productservice.repository.ProductRepository;
import productservice.service.ProductService;

import java.util.List;
import java.util.UUID;

@Validated
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;

    @Override
    public List<Product> getProductsByUserId(@NotNull UUID userId) {
        return productRepository.findAllByUsersContains(userId);
    }
}
