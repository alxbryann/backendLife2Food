package life2food.backend.dto;

import life2food.backend.model.Order;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderByCheckoutResponse {
    /** "completed" cuando el pago ya se procesó y existe la orden; "processing" si aún no */
    private String status;
    private Order order;
}
