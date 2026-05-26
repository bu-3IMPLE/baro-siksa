package com.team3imple.barosiksa.domain.restaurants.dto.request;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record RestaurantSearchRequest(
        @NotNull(message = "현재 위치의 위도가 필요합니다.")
        BigDecimal latitude,

        @NotNull(message = "현재 위치의 경도가 필요합니다.")
        BigDecimal longitude,

        @NotNull(message = "검색 반경(km)은 필수입니다.")
        Double radius
) {
}
