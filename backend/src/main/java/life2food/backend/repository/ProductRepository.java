package life2food.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import life2food.backend.model.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByUserId(Long userId);
}
