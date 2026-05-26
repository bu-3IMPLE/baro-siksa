package com.team3imple.barosiksa.domain.ingredients.dto.response;

import com.team3imple.barosiksa.domain.ingredients.entity.Ingredient;

public record IngredientResponse(
        Long id,
        String name,
        boolean isAllergenic
) {
    public static IngredientResponse from(Ingredient ingredient) {
        return new IngredientResponse(
                ingredient.getId(),
                ingredient.getName(),
                ingredient.isAllergenic()
        );
    }
}