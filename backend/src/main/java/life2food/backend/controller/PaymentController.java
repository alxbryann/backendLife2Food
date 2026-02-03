package life2food.backend.controller;

import life2food.backend.dto.*;
import life2food.backend.service.MercadoPagoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {

    private final MercadoPagoService mercadoPagoService;
    private final String publicKey;

    @Autowired
    public PaymentController(MercadoPagoService mercadoPagoService,
                             @Value("${mercadopago.public-key}") String publicKey) {
        this.mercadoPagoService = mercadoPagoService;
        this.publicKey = publicKey;
    }

    @GetMapping("/config")
    public ResponseEntity<MercadoPagoConfigResponse> getConfig() {
        return ResponseEntity.ok(new MercadoPagoConfigResponse(publicKey));
    }

    @PostMapping("/checkout")
    public ResponseEntity<CheckoutPreferenceResponse> createCheckout(@RequestBody CheckoutPreferenceRequest request) {
        MercadoPagoService.CheckoutResult result = mercadoPagoService.createCheckout(request.getUserId());
        return ResponseEntity.ok(new CheckoutPreferenceResponse(
                result.getCheckoutId(),
                result.getOrderId(),
                result.getInitPoint(),
                result.getPreferenceId()
        ));
    }

    @GetMapping("/order-by-checkout/{checkoutId}")
    public ResponseEntity<?> getOrderByCheckout(@PathVariable Long checkoutId) {
        return ResponseEntity.ok(mercadoPagoService.getOrderByCheckoutId(checkoutId));
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<?> getOrderDetails(@PathVariable Long orderId) {
        return ResponseEntity.ok(mercadoPagoService.getOrderDetails(orderId));
    }

    /** Webhook para notificaciones de Mercado Pago (IPN). URL: https://tu-dominio/api/payment/notification */
    @GetMapping("/notification")
    public ResponseEntity<Void> notification(
            @RequestParam(value = "topic", required = false) String topic,
            @RequestParam(value = "id", required = false) String id) {
        mercadoPagoService.processNotification(topic != null ? topic : "", id != null ? id : "");
        return ResponseEntity.ok().build();
    }
}
