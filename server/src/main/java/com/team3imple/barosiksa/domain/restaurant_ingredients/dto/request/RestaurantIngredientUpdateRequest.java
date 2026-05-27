package com.team3imple.barosiksa.domain.restaurant_ingredients.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record RestaurantIngredientUpdateRequest(
        String origin,

        @NotNull(message = "재고 수량은 필수입니다.")
        @Min(value = 0, message = "재고는 0 이상이어야 합니다.")
        Integer stockQuantity
) {
}
