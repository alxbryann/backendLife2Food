package life2food.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import life2food.backend.model.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
