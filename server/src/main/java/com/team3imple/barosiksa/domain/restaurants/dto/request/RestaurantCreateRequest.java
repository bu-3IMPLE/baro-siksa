package com.team3imple.barosiksa.domain.restaurants.dto.request;

import com.team3imple.barosiksa.domain.restaurants.entity.RestaurantCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalTime;

public record RestaurantCreateRequest(
        @NotBlank(message = "식당 이름은 필수입니다.")
        String name,

        @NotNull(message = "카테고리는 필수입니다.")
        RestaurantCategory category,

        @NotBlank(message = "주소는 필수입니다.")
        String address,

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
