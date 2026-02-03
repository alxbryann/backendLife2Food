package life2food.backend.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Sesión de checkout pendiente de pago.
 * La orden solo se crea cuando Mercado Pago confirma el pago (webhook).
 */
@Data
@Entity
@Table(name = "pending_checkout")
public class PendingCheckout {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "preference_id")
    private String preferenceId;

    @Column(name = "order_id")
    private Long orderId; // asignado cuando el pago se confirma

    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
