package com.team3imple.barosiksa.domain.restaurant_ingredients.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class RestaurantIngredientRepositoryCustomImpl implements RestaurantIngredientRepositoryCustom {
    private final JPAQueryFactory queryFactory;
}
