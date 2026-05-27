package com.team3imple.barosiksa.domain.menus.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.team3imple.barosiksa.domain.menus.dto.response.MenuResponse;
import com.team3imple.barosiksa.domain.menus.dto.response.MenuResponse.MenuIngredientSummary;
import com.team3imple.barosiksa.domain.menus.entity.QMenu;
import com.team3imple.barosiksa.domain.menus.entity.QMenuIngredient;
import com.team3imple.barosiksa.domain.restaurant_ingredients.entity.QRestaurantIngredient;
import com.team3imple.barosiksa.domain.ingredients.entity.QIngredient;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static com.querydsl.core.group.GroupBy.*;

@RequiredArgsConstructor
public class MenuRepositoryCustomImpl implements MenuRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    @Override
    public List<MenuResponse> findMenusWithIngredientsByRestaurantId(Long restaurantId) {
        QMenu menu = QMenu.menu;
        QMenuIngredient menuIngredient = QMenuIngredient.menuIngredient;
        QRestaurantIngredient restaurantIngredient = QRestaurantIngredient.restaurantIngredient;
        QIngredient ingredient = QIngredient.ingredient;

        return queryFactory
                .from(menu)
                .leftJoin(menuIngredient).on(menuIngredient.menu.eq(menu))
                .leftJoin(menuIngredient.restaurantIngredient, restaurantIngredient)
                .leftJoin(restaurantIngredient.ingredient, ingredient)
                .where(menu.restaurant.id.eq(restaurantId))
                .transform(
                        groupBy(menu.id).list(
                                Projections.constructor(MenuResponse.class,
                                        menu.id,
                                        menu.name,
                                        menu.price,
                                        menu.description,
                                        list(Projections.constructor(MenuIngredientSummary.class,
                                                restaurantIngredient.id,
                                                ingredient.name,
                                                ingredient.isAllergenic
                                        ).skipNulls()),
                                        Expressions.constant(false)
                                )
                        )
                );
    }
}
