package com.team3imple.barosiksa.domain.menus.repository;

import com.team3imple.barosiksa.domain.menus.entity.MenuIngredient;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MenuIngredientRepository extends JpaRepository<MenuIngredient, Long>, MenuIngredientRepositoryCustom {
}
