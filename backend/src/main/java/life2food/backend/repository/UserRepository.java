package life2food.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import life2food.backend.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
}
