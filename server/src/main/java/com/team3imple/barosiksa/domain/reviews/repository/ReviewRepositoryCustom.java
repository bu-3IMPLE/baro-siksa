package com.team3imple.barosiksa.domain.reviews.repository;

import com.team3imple.barosiksa.domain.reviews.entity.Review;

import java.util.List;

public interface ReviewRepositoryCustom {
    List<Review> findByRestaurantIdOrderByIdDesc(Long restaurantId);
}
