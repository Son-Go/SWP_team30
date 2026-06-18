package gde.gde_website.store.model;

import java.math.BigDecimal;
import java.time.Instant;

public record Order(
        Long id,
        Long userId,
        OrderStatus status,
        BigDecimal totalPrice,
        Instant createdAt
) {
}
