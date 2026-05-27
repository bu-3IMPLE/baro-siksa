package com.team3imple.barosiksa.domain.restaurants.repository;

import com.team3imple.barosiksa.domain.restaurants.entity.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RestaurantRepository extends JpaRepository<Restaurant, Long>, RestaurantRepositoryCustom {
}
