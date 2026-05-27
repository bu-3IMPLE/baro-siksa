package com.team3imple.barosiksa.domain.menus.repository;

import com.team3imple.barosiksa.domain.menus.dto.response.MenuResponse;

import java.util.List;

public interface MenuRepositoryCustom {
    List<MenuResponse> findMenusWithIngredientsByRestaurantId(Long restaurantId);
}
