package life2food.backend.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import life2food.backend.model.Category;
import life2food.backend.model.Product;
import life2food.backend.repository.CategoryRepository;
import life2food.backend.repository.ProductRepository;

@Component
public class CategoryInitializer implements CommandLineRunner {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductRepository productRepository;

    @Override
    public void run(String... args) throws Exception {
        Category defaultCategory = null;
        
        // Verificar si ya existen categorías
        if (categoryRepository.count() == 0) {
            // Crear las categorías iniciales
            String[] categoryNames = {
                "Frutas",
                "Verduras",
                "Preparados",
                "Almuerzos / menús del día",
                "Desayunos",
                "Cenas",
                "Comida rápida",
                "Comida internacional",
                "Comida saludable / fitness",
                "Comida vegetariana / vegana",
                "Pan del día",
                "Bollería",
                "Pasteles y postres",
                "Snacks listos para comer"
            };

            for (String name : categoryNames) {
                Category category = new Category();
                category.setName(name);
                Category savedCategory = categoryRepository.save(category);
                // Usar la primera categoría como categoría por defecto
                if (defaultCategory == null) {
                    defaultCategory = savedCategory;
                }
            }
        } else {
            // Asegurar que Frutas, Verduras, Preparados existan (por si la DB ya tenía datos)
            String[] quickCategories = { "Frutas", "Verduras", "Preparados" };
            for (String name : quickCategories) {
                if (categoryRepository.findAll().stream().noneMatch(c -> name.equals(c.getName()))) {
                    Category cat = new Category();
                    cat.setName(name);
                    categoryRepository.save(cat);
                }
            }
            defaultCategory = categoryRepository.findAll().stream()
                .findFirst()
                .orElse(null);
        }

        // Asignar categoría por defecto a productos existentes que no tengan categoría
        if (defaultCategory != null) {
            java.util.List<Product> productsWithoutCategory = productRepository.findAll().stream()
                .filter(product -> product.getCategory() == null)
                .toList();
            
            if (!productsWithoutCategory.isEmpty()) {
                for (Product product : productsWithoutCategory) {
                    product.setCategory(defaultCategory);
                    productRepository.save(product);
                }
            }
        }
    }
}
