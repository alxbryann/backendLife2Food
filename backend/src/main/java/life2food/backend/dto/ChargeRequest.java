package life2food.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ChargeRequest {

    @NotNull(message = "orderId es requerido")
    private Long orderId;

    @NotBlank(message = "tokenCard es requerido")
    private String tokenCard;

    @NotBlank(message = "docType es requerido")
    private String docType;

    @NotBlank(message = "docNumber es requerido")
    private String docNumber;

    @NotBlank(message = "name es requerido")
    private String name;

    @NotBlank(message = "lastName es requerido")
    private String lastName;

    @NotBlank(message = "email es requerido")
    private String email;

    private String cellPhone;
    private String address;
    private String city;
}
