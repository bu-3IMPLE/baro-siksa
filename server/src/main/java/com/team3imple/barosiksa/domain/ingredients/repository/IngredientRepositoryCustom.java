package com.team3imple.barosiksa.domain.ingredients.repository;

import com.team3imple.barosiksa.domain.ingredients.entity.Ingredient;

import java.util.List;

public interface IngredientRepositoryCustom {
    List<Ingredient> searchIngredients(String keyword);
}
