package com.team3imple.barosiksa.domain.restaurants.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.team3imple.barosiksa.domain.restaurants.dto.request.RestaurantSearchRequest;
import com.team3imple.barosiksa.domain.restaurants.dto.response.RestaurantSearchCondition;
import com.team3imple.barosiksa.domain.restaurants.entity.QRestaurant;
import com.team3imple.barosiksa.domain.restaurants.entity.Restaurant;
import com.team3imple.barosiksa.domain.restaurants.entity.RestaurantCategory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.util.StringUtils;

import java.util.List;

@RequiredArgsConstructor
public class RestaurantRepositoryCustomImpl implements RestaurantRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    @Override
    public List<Restaurant> findByRadius(RestaurantSearchRequest condition) {
        QRestaurant restaurant = QRestaurant.restaurant;

        // 구면 코사인 법칙을 이용한 거리(km) 계산 SQL 템플릿 연산식
        NumberExpression<Double> distanceExpression = Expressions.numberTemplate(Double.class,
                "6371 * acos(cos(radians({0})) * cos(radians({1})) * cos(radians({2}) - radians({3})) + sin(radians({0})) * sin(radians({1})))",
                condition.latitude(), restaurant.latitude, restaurant.longitude, condition.longitude());

        return queryFactory
                .selectFrom(restaurant)
                .where(
                        distanceExpression.loe(condition.radius()), // 계산된 거리가 요청한 radius 이하인 것만
                        restaurant.isDeleted.eq(false)             // 폐업하지 않은 식당만
                )
                .orderBy(distanceExpression.asc()) // 가까운 순으로 정렬
                .fetch();
    }

    @Override
    public Page<Restaurant> searchRestaurants(RestaurantSearchCondition condition, Pageable pageable) {
        QRestaurant restaurant = QRestaurant.restaurant;

        List<Restaurant> content = queryFactory
                .selectFrom(restaurant)
                .where(
                        restaurant.isDeleted.eq(false),
                        nameContains(condition.name()),
                        categoryEq(condition.category())
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(restaurant.id.desc())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(restaurant.count())
                .from(restaurant)
                .where(
                        restaurant.isDeleted.eq(false),
                        nameContains(condition.name()),
                        categoryEq(condition.category())
                );
        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    // 동적 쿼리를 위한 도우미 메서드들
    private BooleanExpression nameContains(String name) {
        return StringUtils.hasText(name) ? QRestaurant.restaurant.name.contains(name) : null;
    }

    private BooleanExpression categoryEq(RestaurantCategory category) {
        return category != null ? QRestaurant.restaurant.category.eq(category) : null;
    }
}
