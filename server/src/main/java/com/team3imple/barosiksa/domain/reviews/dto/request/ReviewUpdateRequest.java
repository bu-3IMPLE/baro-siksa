package com.team3imple.barosiksa.domain.reviews.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record ReviewUpdateRequest(
        @NotNull(message = "별점은 필수입니다.")
        @Min(value = 1, message = "별점은 최소 1점 이상이어야 합니다.")
        @Max(value = 5, message = "별점은 최대 5점 이하이어야 합니다.")
        Integer rating,

        String comment
) {
}
