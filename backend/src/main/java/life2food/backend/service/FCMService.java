package life2food.backend.service;

import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnBean(FirebaseApp.class)
public class FCMService {

    public String sendNotification(String token, String title, String body) {
        try {
            Message message = Message.builder()
                    .setToken(token)
                    .setNotification(Notification.builder()
                            .setTitle(title)
                            .setBody(body)
                            .build())
                    .build();
            return FirebaseMessaging.getInstance().send(message);
        } catch (Exception e) {
            e.printStackTrace();
            return "Error sending notification: " + e.getMessage();
        }
    }
}
