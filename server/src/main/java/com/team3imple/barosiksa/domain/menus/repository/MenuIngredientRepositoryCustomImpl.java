package com.team3imple.barosiksa.domain.menus.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.team3imple.barosiksa.domain.menus.entity.QMenuIngredient;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MenuIngredientRepositoryCustomImpl implements MenuIngredientRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    @Override
    public void deleteByMenuId(Long menuId) {
        QMenuIngredient menuIngredient = QMenuIngredient.menuIngredient;

        queryFactory.delete(menuIngredient)
                .where(menuIngredient.menu.id.eq(menuId))
                .execute();
    }
}
