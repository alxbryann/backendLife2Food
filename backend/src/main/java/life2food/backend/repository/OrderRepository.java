package life2food.backend.repository;

import life2food.backend.model.Order;
import life2food.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUser(User user);

    @Query("SELECT DISTINCT o FROM Order o JOIN o.items i WHERE i.product.user.id = :storeOwnerId ORDER BY o.createdAt DESC")
    List<Order> findOrdersByStoreOwner(@Param("storeOwnerId") Long storeOwnerId);
}
