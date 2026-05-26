package com.team3imple.barosiksa.domain.menus.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record MenuCreateRequest(
        @NotBlank(message = "메뉴 이름은 필수입니다.")
        String name,

        @NotNull(message = "가격은 필수입니다.")
        @Min(value = 0, message = "가격은 0원 이상이어야 합니다.")
        Integer price,

        String description,

        List<Long> restaurantIngredientIds
) {
}
