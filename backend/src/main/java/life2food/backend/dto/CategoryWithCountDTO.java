package life2food.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryWithCountDTO {
    private Long id;
    private String name;
    private long productCount;
}
