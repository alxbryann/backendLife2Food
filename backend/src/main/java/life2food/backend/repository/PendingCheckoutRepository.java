package life2food.backend.repository;

import life2food.backend.model.PendingCheckout;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PendingCheckoutRepository extends JpaRepository<PendingCheckout, Long> {
}
