package life2food.backend.service;

import com.fasterxml.jackson.annotation.JsonProperty;
import life2food.backend.dto.OrderByCheckoutResponse;
import life2food.backend.model.*;
import life2food.backend.repository.*;
import life2food.backend.service.OrderService;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class MercadoPagoService {

    private static final String MP_PREFERENCES_URL = "https://api.mercadopago.com/checkout/preferences";
    private static final String MP_PAYMENTS_URL = "https://api.mercadopago.com/v1/payments";
    private static final String CURRENCY_ID = "COP";

    @Value("${mercadopago.access-token}")
    private String accessToken;

    @Value("${mercadopago.notification-url:}")
    private String notificationUrl;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PendingCheckoutRepository pendingCheckoutRepository;

    @Autowired
    private OrderService orderService;

    /**
     * Crea una sesión de checkout (PendingCheckout) y una preferencia de Mercado Pago.
     * No se crea la orden ni se vacía el carrito hasta que el pago esté confirmado (webhook).
     */
    @Transactional
    public CheckoutResult createCheckout(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Carrito no encontrado"));

        if (cart.getItems() == null || cart.getItems().isEmpty()) {
            throw new RuntimeException("El carrito está vacío");
        }

        PendingCheckout pending = new PendingCheckout();
        pending.setUser(user);
        pending = pendingCheckoutRepository.save(pending);

        // Construir items para Mercado Pago (unit_price debe ser entero; en COP se envía en centavos)
        List<Map<String, Object>> mpItems = cart.getItems().stream().map(cartItem -> {
            Product p = cartItem.getProduct();
            double price = p.getPrice() != null ? p.getPrice() : 0;
            int unitPriceCents = (int) Math.round(price * 100);
            Map<String, Object> item = new HashMap<>();
            item.put("title", p.getName() != null ? p.getName() : "Producto");
            item.put("quantity", cartItem.getQuantity());
            item.put("unit_price", unitPriceCents);
            item.put("currency_id", CURRENCY_ID);
            if (p.getImageUrl() != null && !p.getImageUrl().isEmpty()) {
                item.put("picture_url", p.getImageUrl());
            }
            return item;
        }).collect(Collectors.toList());

        Map<String, Object> payer = new HashMap<>();
        payer.put("email", user.getEmail() != null ? user.getEmail() : "comprador@life2food.com");
        payer.put("name", user.getFirst_name() != null ? user.getFirst_name() : "Comprador");
        payer.put("surname", user.getLast_name() != null ? user.getLast_name() : "Life2Food");

        // No excluir ningún método: tarjeta crédito/débito, Mercado Pago (wallet) y PSE
        Map<String, Object> paymentMethods = new HashMap<>();
        paymentMethods.put("excluded_payment_methods", Collections.emptyList());
        paymentMethods.put("excluded_payment_types", Collections.emptyList());

        Map<String, String> backUrls = new HashMap<>();
        backUrls.put("success", "life2food://payment/success");
        backUrls.put("failure", "life2food://payment/failure");
        backUrls.put("pending", "life2food://payment/pending");

        Map<String, Object> body = new HashMap<>();
        body.put("items", mpItems);
        body.put("payer", payer);
        body.put("payment_methods", paymentMethods);
        body.put("back_urls", backUrls);
        body.put("auto_return", "approved");
        body.put("external_reference", pending.getId().toString());
        if (notificationUrl != null && !notificationUrl.isEmpty()) {
            body.put("notification_url", notificationUrl);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + accessToken);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
        ResponseEntity<MercadoPagoPreferenceResponse> response = restTemplate.exchange(
                MP_PREFERENCES_URL,
                HttpMethod.POST,
                request,
                MercadoPagoPreferenceResponse.class
        );

        if (response.getStatusCode() != HttpStatus.CREATED && response.getStatusCode() != HttpStatus.OK) {
            throw new RuntimeException("Error al crear preferencia de Mercado Pago");
        }

        MercadoPagoPreferenceResponse mpResponse = response.getBody();
        if (mpResponse == null || mpResponse.getInitPoint() == null) {
            throw new RuntimeException("Respuesta inválida de Mercado Pago");
        }

        pending.setPreferenceId(mpResponse.getId());
        pendingCheckoutRepository.save(pending);

        return new CheckoutResult(
                pending.getId(),
                null,
                mpResponse.getInitPoint(),
                mpResponse.getId()
        );
    }

    public Order getOrderDetails(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));
    }

    /**
     * Devuelve la orden cuando el pago ya fue confirmado para este checkout. Si aún no se procesó, status "processing".
     */
    public OrderByCheckoutResponse getOrderByCheckoutId(Long checkoutId) {
        return pendingCheckoutRepository.findById(checkoutId)
                .map(p -> {
                    if (p.getOrderId() != null) {
                        Order order = orderRepository.findById(p.getOrderId()).orElse(null);
                        return new OrderByCheckoutResponse("completed", order);
                    }
                    return new OrderByCheckoutResponse("processing", null);
                })
                .orElse(new OrderByCheckoutResponse("processing", null));
    }

    /**
     * Procesa notificación de Mercado Pago (IPN). Si el pago está aprobado, crea la orden desde el carrito y la marca como PAID.
     */
    public void processNotification(String topic, String paymentId) {
        if (!"payment".equals(topic) || paymentId == null || paymentId.isEmpty()) {
            return;
        }
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        HttpEntity<Void> request = new HttpEntity<>(headers);
        try {
            ResponseEntity<MercadoPagoPaymentResponse> response = restTemplate.exchange(
                    MP_PAYMENTS_URL + "/" + paymentId,
                    HttpMethod.GET,
                    request,
                    MercadoPagoPaymentResponse.class
            );
            if (response.getBody() != null && "approved".equals(response.getBody().getStatus())) {
                String ref = response.getBody().getExternalReference();
                if (ref != null && !ref.isEmpty()) {
                    createOrderFromPayment(ref);
                }
            }
        } catch (Exception e) {
            // Log and ignore to avoid MP retries failing
        }
    }

    /**
     * Crea la orden desde el carrito del usuario y la marca como PAID. Solo se llama cuando el pago está aprobado.
     */
    @Transactional
    public void createOrderFromPayment(String pendingCheckoutIdStr) {
        Long pendingId = Long.parseLong(pendingCheckoutIdStr);
        PendingCheckout pending = pendingCheckoutRepository.findById(pendingId).orElse(null);
        if (pending == null || pending.getOrderId() != null) {
            return; // ya procesado o no existe
        }
        Long userId = pending.getUser().getId();
        Order order = orderService.checkoutAndSetPaid(userId);
        pending.setOrderId(order.getId());
        pendingCheckoutRepository.save(pending);
    }

    @Data
    public static class CheckoutResult {
        private final Long checkoutId;
        private final Long orderId;
        private final String initPoint;
        private final String preferenceId;
    }

    @Data
    private static class MercadoPagoPreferenceResponse {
        private String id;
        @JsonProperty("init_point")
        private String initPoint;
    }

    @Data
    private static class MercadoPagoPaymentResponse {
        private String status;
        @JsonProperty("external_reference")
        private String externalReference;
    }
}
