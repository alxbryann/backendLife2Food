package life2food.backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins(
                    "http://localhost:8080",
                    "http://localhost:4200",
                    "https://api.life2food.com",
                    "https://owners.life2food.com"
                )
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
        
        // Permitir CORS específicamente para OpenAPI
        registry.addMapping("/v3/api-docs/**")
                .allowedOrigins(
                    "http://localhost:8080",
                    "http://localhost:4200",
                    "https://api.life2food.com",
                    "https://owners.life2food.com"
                )
                .allowedMethods("GET", "OPTIONS")
                .allowedHeaders("*")
                .maxAge(3600);
        
        registry.addMapping("/swagger-ui/**")
                .allowedOrigins(
                    "http://localhost:8080",
                    "http://localhost:4200",
                    "https://api.life2food.com",
                    "https://owners.life2food.com"
                )
                .allowedMethods("GET", "OPTIONS")
                .allowedHeaders("*")
                .maxAge(3600);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/openapi/**")
                .addResourceLocations("classpath:/openapi/");
    }
}
