package com.team3imple.barosiksa.domain.tables.repository;

import com.team3imple.barosiksa.domain.tables.entity.RestaurantTable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TableRepository extends JpaRepository<RestaurantTable, Long> {
    List<RestaurantTable> findByRestaurantIdOrderByTableNumber(Long restaurantId);
}
