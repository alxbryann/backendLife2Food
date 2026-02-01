package life2food.backend.controller;

import life2food.backend.model.Order;
import life2food.backend.repository.OrderRepository;
import life2food.backend.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private OrderRepository orderRepository;

    @GetMapping("/epayco-params/{orderId}")
    public ResponseEntity<Map<String, String>> getEpaycoParams(@PathVariable Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        String amount = String.valueOf(order.getTotalPrice());
        String currency = "COP"; // Default to COP
        String invoiceId = String.valueOf(order.getId());

        String signature = paymentService.generateSignature(invoiceId, amount, currency);

        Map<String, String> params = new HashMap<>();
        params.put("p_cust_id_cliente", paymentService.getCustId());
        params.put("p_key", paymentService.getPublicKey());
        params.put("p_id_invoice", invoiceId);
        params.put("p_amount", amount);
        params.put("p_currency", currency);
        params.put("p_signature", signature);
        params.put("p_test_request", "TRUE"); // Default to Test Mode
        params.put("p_description", "Order #" + invoiceId);

        return ResponseEntity.ok(params);
    }

    @RequestMapping(
            value = "/response",
            method = {RequestMethod.GET, RequestMethod.POST},
            consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE, MediaType.ALL_VALUE},
            produces = MediaType.TEXT_HTML_VALUE
    )
    public ResponseEntity<String> handleResponse(@RequestParam Map<String, String> params) {
        updateOrderStatusIfPossible(params);
        String html = "<html><body style='font-family:sans-serif;text-align:center;padding:40px;'>"
                + "<h2>Pago procesado</h2>"
                + "<p>Ya puedes volver a la app.</p>"
                + "</body></html>";
        return ResponseEntity.ok(html);
    }

    @RequestMapping(
            value = "/confirmation",
            method = {RequestMethod.GET, RequestMethod.POST},
            consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE, MediaType.ALL_VALUE}
    )
    public ResponseEntity<Map<String, Object>> handleConfirmation(@RequestParam Map<String, String> params) {
        updateOrderStatusIfPossible(params);
        Map<String, Object> result = new HashMap<>();
        result.put("received", true);
        return ResponseEntity.ok(result);
    }

    private void updateOrderStatusIfPossible(Map<String, String> params) {
        String orderIdValue = params.getOrDefault("x_id_invoice",
                params.getOrDefault("p_id_invoice", params.get("orderId")));

        if (orderIdValue == null) {
            return;
        }

        try {
            Long orderId = Long.valueOf(orderIdValue);
            orderRepository.findById(orderId).ifPresent(order -> {
                String responseCode = params.getOrDefault("x_cod_response", params.get("x_response"));
                if (responseCode != null && ("1".equals(responseCode) || "Aceptada".equalsIgnoreCase(responseCode))) {
                    order.setStatus("PAID");
                } else if (responseCode != null) {
                    order.setStatus("PENDING");
                }
                orderRepository.save(order);
            });
        } catch (NumberFormatException ignored) {
        }
    }
}
