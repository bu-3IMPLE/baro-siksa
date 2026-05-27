package com.team3imple.barosiksa.domain.reviews.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.team3imple.barosiksa.domain.reviews.entity.Review;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static com.team3imple.barosiksa.domain.reviews.entity.QReview.review;

@RequiredArgsConstructor
public class ReviewRepositoryImpl implements ReviewRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    @Override
    public List<Review> findByRestaurantIdOrderByIdDesc(Long restaurantId) {
        return queryFactory
                .selectFrom(review)
                .where(review.restaurant.id.eq(restaurantId))
                .orderBy(review.id.desc())
                .fetch();
    }
}
