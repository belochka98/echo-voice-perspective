package productservice.service;

import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;
import productservice.entity.Product;

import java.util.List;
import java.util.UUID;

@Validated
public interface ProductService {
    List<Product> getProductsByUserId(@NotNull UUID userId);
}
