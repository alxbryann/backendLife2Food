package life2food.backend.service;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Sends push notifications via Expo's push API (no Firebase).
 * API: https://docs.expo.dev/push-notifications/sending-notifications/
 */
@Service
public class ExpoPushService {

    private static final String EXPO_PUSH_URL = "https://exp.host/--/api/v2/push/send";

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper()
            .setSerializationInclusion(JsonInclude.Include.NON_NULL);

    /**
     * @param expoPushToken Token from the app (ExponentPushToken[...] or Expo push token)
     * @param title         Notification title
     * @param body          Notification body
     */
    public void sendNotification(String expoPushToken, String title, String body) {
        if (expoPushToken == null || expoPushToken.isEmpty()) {
            return;
        }
        sendNotification(expoPushToken, title, body, null);
    }

    public void sendNotification(String expoPushToken, String title, String body, Map<String, Object> data) {
        if (expoPushToken == null || expoPushToken.isEmpty()) {
            return;
        }
        try {
            ExpoMessage message = new ExpoMessage();
            message.setTo(expoPushToken);
            message.setTitle(title);
            message.setBody(body);
            message.setData(data != null ? data : Collections.emptyMap());

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

            String bodyJson = objectMapper.writeValueAsString(List.of(message));
            HttpEntity<String> request = new HttpEntity<>(bodyJson, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(EXPO_PUSH_URL, request, String.class);

            if (!response.getStatusCode().is2xxSuccessful()) {
                System.err.println("Expo push failed: " + response.getStatusCode() + " " + response.getBody());
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error sending Expo push: " + e.getMessage());
        }
    }

    @lombok.Data
    public static class ExpoMessage {
        private String to;
        private String title;
        private String body;
        private Map<String, Object> data;
        private String sound = "default";
        private Integer badge;
        private String channelId;
    }
}
