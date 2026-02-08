package life2food.backend.model;

import lombok.Data;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.CascadeType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@jakarta.persistence.Table(name = "user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String first_name;

    private String last_name;

    private String email;

    private String photo_url;

    private String address;

    private double latitude;

    private double longitude;

    private boolean isBusiness;

    private String fcmToken;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Cart> carts = new ArrayList<>();

}
