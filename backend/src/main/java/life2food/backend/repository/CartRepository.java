package life2food.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import life2food.backend.model.Cart;
import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {
    Optional<Cart> findByUserId(Long userId);
}
