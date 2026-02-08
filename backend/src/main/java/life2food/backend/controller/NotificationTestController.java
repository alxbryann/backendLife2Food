package life2food.backend.controller;

import life2food.backend.model.Order;
import life2food.backend.model.OrderItem;
import life2food.backend.model.User;
import life2food.backend.repository.OrderRepository;
import life2food.backend.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/test")
public class NotificationTestController {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private life2food.backend.service.NotificationService notificationService;

    @GetMapping("/notify/{orderId}")
    public String testNotification(@PathVariable Long orderId) {
        Order order = orderRepository.findById(orderId).orElse(null);
        if (order == null) {
            return "Order not found";
        }

        if (order.getItems() == null || order.getItems().isEmpty()) {
            return "Order has no items";
        }

        // Group items by store owner
        Map<User, List<OrderItem>> itemsByStoreOwner = order.getItems().stream()
                .collect(Collectors.groupingBy(item -> item.getProduct().getUser()));

        StringBuilder result = new StringBuilder();
        itemsByStoreOwner.forEach((storeOwner, items) -> {
            emailService.sendOrderNotificationToStore(storeOwner, items, order);
            notificationService.notifyStoreOwner(storeOwner, items, order);
            result.append("Sent notification to: ").append(storeOwner.getEmail()).append("\n");
        });

        return "Notification process triggered.\n" + result.toString();
    }
}
