package life2food.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<life2food.backend.model.User, Long> {
}
