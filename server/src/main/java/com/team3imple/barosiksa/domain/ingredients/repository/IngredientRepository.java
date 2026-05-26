package com.team3imple.barosiksa.domain.ingredients.repository;

import com.team3imple.barosiksa.domain.ingredients.entity.Ingredient;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IngredientRepository extends JpaRepository<Ingredient, Long>, IngredientRepositoryCustom {
}
