package com.team3imple.barosiksa.domain.menus.dto.response;

import java.util.List;

public record MenuResponse(
        Long id,
        String name,
        Integer price,
        String description,
        List<MenuIngredientSummary> ingredients,
        boolean inDangerous
) {
    public MenuResponse markAsDangerous(boolean dangerous) {
        return new MenuResponse(id, name, price, description, ingredients, dangerous);
    }

    public record MenuIngredientSummary(
            Long restaurantIngredientId,
            String name,
            boolean isAllergenic
    ) {
    }
}