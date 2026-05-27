package com.team3imple.barosiksa.domain.restaurant_ingredients.repository;

import com.team3imple.barosiksa.domain.restaurant_ingredients.entity.RestaurantIngredient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RestaurantIngredientRepository extends JpaRepository<RestaurantIngredient, Long>, RestaurantIngredientRepositoryCustom {
    List<RestaurantIngredient> findByRestaurantId(Long restaurantId);
}
