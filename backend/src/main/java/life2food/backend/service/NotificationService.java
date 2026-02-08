package life2food.backend.service;

import life2food.backend.model.Order;
import life2food.backend.model.OrderItem;
import life2food.backend.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationService {

    @Autowired
    private ExpoPushService expoPushService;

    /**
     * Sends a push notification to the store owner. Uses Expo push (no Firebase).
     * The user's fcmToken field stores the Expo push token.
     */
    public void notifyStoreOwner(User storeOwner, List<OrderItem> items, Order order) {
        String pushToken = storeOwner.getFcmToken();
        if (pushToken == null || pushToken.isEmpty()) {
            System.out.println("No push token for user: " + storeOwner.getEmail());
            return;
        }

        String title = "New Order Received!";
        String body = "Order #" + order.getId() + " contains " + items.size() + " items.";

        expoPushService.sendNotification(pushToken, title, body);
    }
}
