package life2food.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CheckoutPreferenceResponse {
    /** Id de la sesión de checkout; usar para consultar la orden una vez pagado */
    private Long checkoutId;
    /** Id de la orden (solo presente después de pago confirmado) */
    private Long orderId;
    private String initPoint;
    private String preferenceId;
}
