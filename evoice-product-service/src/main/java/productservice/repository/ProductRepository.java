package productservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.history.RevisionRepository;
import productservice.entity.Product;

import java.util.List;
import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID>, RevisionRepository<Product, UUID, Long> {
    List<Product> findAllByUsersContains(UUID userId);
}
