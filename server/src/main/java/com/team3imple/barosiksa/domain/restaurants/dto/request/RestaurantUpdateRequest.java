package com.team3imple.barosiksa.domain.restaurants.dto.request;

import com.team3imple.barosiksa.domain.restaurants.entity.RestaurantCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalTime;

public record RestaurantUpdateRequest(
        @NotBlank(message = "식당 이름은 필수입니다.")
        String name,

        @NotNull(message = "카테고리는 필수입니다.")
        RestaurantCategory category,

        @NotBlank(message = "주소는 필수입니다.")
        String address,

        @NotNull(message = "위도는 필수입니다.")
        BigDecimal latitude,

        @NotNull(message = "경도는 필수입니다.")
        BigDecimal longitude,

        String phoneNumber,
        String description,

        @NotNull(message = "오픈 시간은 필수입니다.")
        LocalTime openTime,

        @NotNull(message = "마감 시간은 필수입니다.")
        LocalTime closeTime,

        LocalTime breakStartTime,

        LocalTime breakEndTime,

        String closedDays
) {
}
