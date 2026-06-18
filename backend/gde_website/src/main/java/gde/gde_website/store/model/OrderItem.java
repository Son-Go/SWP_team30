package gde.gde_website.store.model;

import java.math.BigDecimal;

public record OrderItem(
        Long id,
        Long orderId,
        Long productId,
        Integer quantity,
        BigDecimal priceAtPurchase
) {
}
