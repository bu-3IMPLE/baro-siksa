package com.team3imple.barosiksa.domain.ingredients.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record IngredientCreateRequest(
        @NotBlank(message = "재료 이름은 필수입니다.")
        String name,

        @NotNull(message = "알레르기 유발 여부를 선택해주세요.")
        Boolean isAllergenic
) {
}