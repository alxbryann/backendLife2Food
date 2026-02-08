package life2food.backend.controller;

import life2food.backend.model.Order;
import life2food.backend.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping("/checkout/{userId}")
    public ResponseEntity<Order> checkout(@PathVariable Long userId) {
        try {
            Order order = orderService.checkout(userId);
            return ResponseEntity.ok(order);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    /** Órdenes que contienen productos del vendedor (ruta más específica antes que /{userId}) */
    @GetMapping("/store/{storeOwnerId}")
    public ResponseEntity<List<Order>> getOrdersByStoreOwner(@PathVariable Long storeOwnerId) {
        try {
            List<Order> orders = orderService.getOrdersByStoreOwner(storeOwnerId);
            return ResponseEntity.ok(orders);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(null);
        }
    }

    @PatchMapping("/{orderId}/status")
    public ResponseEntity<Order> updateOrderStatus(@PathVariable Long orderId, @RequestBody java.util.Map<String, String> body) {
        try {
            String status = body != null && body.containsKey("status") ? body.get("status") : null;
            if (status == null || status.isBlank()) {
                return ResponseEntity.badRequest().build();
            }
            Order order = orderService.updateOrderStatus(orderId, status);
            return ResponseEntity.ok(order);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<Order>> getUserOrders(@PathVariable Long userId) {
        try {
            List<Order> orders = orderService.getUserOrders(userId);
            return ResponseEntity.ok(orders);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
