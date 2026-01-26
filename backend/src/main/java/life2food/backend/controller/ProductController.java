package life2food.backend.controller;

import java.util.List;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import life2food.backend.model.Product;
import life2food.backend.repository.ProductRepository;
import life2food.backend.service.S3Service;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;



@RestController
@RequestMapping("/products")
public class ProductController {

    @Autowired
    public ProductRepository productRepository;

    @Autowired
    private S3Service s3Service;

    @GetMapping()
    public List<Product> getAllProducts(){
        return productRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        return productRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Product createProduct(@RequestPart("product") Product product, @RequestPart("image") MultipartFile image) throws IOException {
        String imageUrl = s3Service.uploadFile(image);
        product.setImageUrl(imageUrl);
        return productRepository.save(product);
    }
    
    @PatchMapping("/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable Long id, @RequestBody Product product) {
        Product existingProduct = productRepository.findById(id).orElse(null);
        if (existingProduct == null) {
            return ResponseEntity.notFound().build();
        }
        if (product.getName() != null) {
            existingProduct.setName(product.getName());
        }
        if (product.getDescription() != null) {
            existingProduct.setDescription(product.getDescription());
        }
        if (product.getPrice() != null) {
            existingProduct.setPrice(product.getPrice());
        }
        if (product.getAmount() != null) {
            existingProduct.setAmount(product.getAmount());
        }
        if (product.getExpirationDate() != null) {
            existingProduct.setExpirationDate(product.getExpirationDate());
        }
        if (product.getUser() != null) {
            existingProduct.setUser(product.getUser());
        }
        return ResponseEntity.ok(productRepository.save(existingProduct));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        if (!productRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        productRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }



}
