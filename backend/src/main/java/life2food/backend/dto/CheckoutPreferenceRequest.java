package life2food.backend.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CheckoutPreferenceRequest {
    @NotNull(message = "userId es requerido")
    private Long userId;
}
