package life2food.backend.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        Server productionServer = new Server();
        productionServer.setUrl("https://api.life2food.com");
        productionServer.setDescription("Production");

        Server localServer = new Server();
        localServer.setUrl("http://localhost:8080");
        localServer.setDescription("Local");

        return new OpenAPI()
                .servers(List.of(productionServer, localServer));
    }
}
