package com.team3imple.barosiksa.domain.restaurants.dto.response;

import com.team3imple.barosiksa.domain.restaurants.entity.RestaurantCategory;

public record RestaurantSearchCondition(
        String name,
        RestaurantCategory category
) {
}
