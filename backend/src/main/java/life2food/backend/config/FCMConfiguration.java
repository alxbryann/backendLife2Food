package life2food.backend.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnResource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;

@Configuration
@ConditionalOnResource(resources = "classpath:serviceAccountKey.json")
public class FCMConfiguration {

    @Value("${firebase.config.path:serviceAccountKey.json}")
    private String firebaseConfigPath;

    @Bean
    public FirebaseApp firebaseApp() throws IOException {
        if (FirebaseApp.getApps().isEmpty()) {
            try (InputStream serviceAccount = new ClassPathResource(firebaseConfigPath).getInputStream()) {
                FirebaseOptions options = FirebaseOptions.builder()
                        .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                        .build();
                return FirebaseApp.initializeApp(options);
            }
        }
        return FirebaseApp.getInstance();
    }
}
