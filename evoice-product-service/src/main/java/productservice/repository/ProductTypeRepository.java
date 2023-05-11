package productservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import productservice.entity.ProductType;

import java.util.UUID;

public interface ProductTypeRepository extends JpaRepository<ProductType, UUID> {
}
