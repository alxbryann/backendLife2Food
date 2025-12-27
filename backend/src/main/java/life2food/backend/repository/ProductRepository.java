package life2food.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import life2food.backend.model.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {

}
