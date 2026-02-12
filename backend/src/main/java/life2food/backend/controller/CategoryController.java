package life2food.backend.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import life2food.backend.dto.CategoryWithCountDTO;
import life2food.backend.model.Category;
import life2food.backend.repository.CategoryRepository;
import life2food.backend.repository.ProductRepository;

@RestController
@RequestMapping("/categories")
public class CategoryController {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductRepository productRepository;

    @GetMapping()
    public List<CategoryWithCountDTO> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(c -> new CategoryWithCountDTO(
                        c.getId(),
                        c.getName(),
                        productRepository.countByCategoryId(c.getId())))
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Category> getCategoryById(@PathVariable Long id) {
        return categoryRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

}
