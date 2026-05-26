package com.team3imple.barosiksa.domain.restaurants.dto.response;

import com.team3imple.barosiksa.domain.restaurants.entity.Restaurant;

import java.math.BigDecimal;
import java.time.LocalTime;

public record RestaurantResponse(
        Long id,
        String name,
        String category,
        String address,
        BigDecimal latitude,
        BigDecimal longitude,
        String phoneNumber,
        String description,
        LocalTime openTime,
        LocalTime closeTime,
        LocalTime breakStartTime,
        LocalTime breakEndTime,
        String closedDays
) {
    public static RestaurantResponse from(Restaurant restaurant) {
        return new RestaurantResponse(
                restaurant.getId(),
                restaurant.getName(),
                restaurant.getCategory().name(),
                restaurant.getAddress(),
                restaurant.getLatitude(),
                restaurant.getLongitude(),
                restaurant.getPhoneNumber(),
                restaurant.getDescription(),
                restaurant.getOpenTime(),
                restaurant.getCloseTime(),
                restaurant.getBreakStartTime(),
                restaurant.getBreakEndTime(),
                restaurant.getClosedDays()
        );
    }
}