package gde.gde_website.store.model;

import java.math.BigDecimal;

public record Product(
        Long id,
        String name,
        String description,
        BigDecimal price,
        Integer stockQuantity,
        String imageURL
) {
}
