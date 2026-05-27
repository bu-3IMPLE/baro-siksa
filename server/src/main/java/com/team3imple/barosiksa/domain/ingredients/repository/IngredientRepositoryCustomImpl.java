package com.team3imple.barosiksa.domain.ingredients.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.team3imple.barosiksa.domain.ingredients.entity.Ingredient;
import com.team3imple.barosiksa.domain.ingredients.entity.QIngredient;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;

import java.util.List;

@RequiredArgsConstructor
public class IngredientRepositoryCustomImpl implements IngredientRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    @Override
    public List<Ingredient> searchIngredients(String keyword) {
        QIngredient ingredient = QIngredient.ingredient;

        return queryFactory
                .selectFrom(ingredient)
                .where(nameContains(keyword))
                .fetch();
    }

    private BooleanExpression nameContains(String keyword) {
        return StringUtils.hasText(keyword) ? QIngredient.ingredient.name.contains(keyword) : null;
    }
}
