package com.team3imple.barosiksa.domain.restaurants.repository;

import com.team3imple.barosiksa.domain.restaurants.dto.request.RestaurantSearchRequest;
import com.team3imple.barosiksa.domain.restaurants.dto.response.RestaurantSearchCondition;
import com.team3imple.barosiksa.domain.restaurants.entity.Restaurant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface RestaurantRepositoryCustom {
    List<Restaurant> findByRadius(RestaurantSearchRequest condition);

    Page<Restaurant> searchRestaurants(RestaurantSearchCondition condition, Pageable pageable);
}
