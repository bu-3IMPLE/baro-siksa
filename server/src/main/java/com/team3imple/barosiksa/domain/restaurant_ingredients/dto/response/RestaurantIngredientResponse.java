package com.team3imple.barosiksa.domain.restaurant_ingredients.dto.response;

import com.team3imple.barosiksa.domain.restaurant_ingredients.entity.RestaurantIngredient;

public record RestaurantIngredientResponse(
        Long restaurantIngredientId,
        Long ingredientId, // 마스터 재료 ID
        String name,       // 마스터 재료 이름
        boolean isAllergenic,
        String origin,
        Integer stockQuantity
) {
    public static RestaurantIngredientResponse from(RestaurantIngredient restaurantIngredient) {
        return new RestaurantIngredientResponse(
                restaurantIngredient.getId(),
                restaurantIngredient.getIngredient().getId(),
                restaurantIngredient.getIngredient().getName(),
                restaurantIngredient.getIngredient().isAllergenic(),
                restaurantIngredient.getOrigin(),
                restaurantIngredient.getStockQuantity()
        );
    }
}
