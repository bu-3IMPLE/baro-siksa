package com.team3imple.barosiksa.domain.menus.repository;

import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.team3imple.barosiksa.domain.menus.dto.response.MenuResponse;
import com.team3imple.barosiksa.domain.menus.dto.response.MenuResponse.MenuIngredientSummary;
import com.team3imple.barosiksa.domain.menus.entity.QMenu;
import com.team3imple.barosiksa.domain.menus.entity.QMenuIngredient;
import com.team3imple.barosiksa.domain.restaurant_ingredients.entity.QRestaurantIngredient;
import com.team3imple.barosiksa.domain.ingredients.entity.QIngredient;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class MenuRepositoryCustomImpl implements MenuRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    @Override
    public List<MenuResponse> findMenusWithIngredientsByRestaurantId(Long restaurantId) {
        QMenu menu = QMenu.menu;
        QMenuIngredient menuIngredient = QMenuIngredient.menuIngredient;
        QRestaurantIngredient restaurantIngredient = QRestaurantIngredient.restaurantIngredient;
        QIngredient ingredient = QIngredient.ingredient;

        // fetch()는 Hibernate 6와 호환 (transform/GroupBy는 Hibernate 6에서 NoSuchMethodError 발생)
        List<Tuple> rows = queryFactory
                .select(menu.id, menu.name, menu.price, menu.description,
                        restaurantIngredient.id, ingredient.name, ingredient.isAllergenic)
                .from(menu)
                .leftJoin(menuIngredient).on(menuIngredient.menu.eq(menu))
                .leftJoin(menuIngredient.restaurantIngredient, restaurantIngredient)
                .leftJoin(restaurantIngredient.ingredient, ingredient)
                .where(menu.restaurant.id.eq(restaurantId))
                .orderBy(menu.id.asc())
                .fetch();

        Map<Long, MenuResponse> map = new LinkedHashMap<>();
        for (Tuple row : rows) {
            Long menuId = row.get(menu.id);
            Long riId = row.get(restaurantIngredient.id);

            MenuIngredientSummary summary = riId != null
                    ? new MenuIngredientSummary(riId, row.get(ingredient.name),
                            Boolean.TRUE.equals(row.get(ingredient.isAllergenic)))
                    : null;

            if (!map.containsKey(menuId)) {
                List<MenuIngredientSummary> ingredients = new ArrayList<>();
                if (summary != null) ingredients.add(summary);
                map.put(menuId, new MenuResponse(menuId, row.get(menu.name),
                        row.get(menu.price), row.get(menu.description), ingredients, false));
            } else if (summary != null) {
                map.get(menuId).ingredients().add(summary);
            }
        }
        return new ArrayList<>(map.values());
    }
}
