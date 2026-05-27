package com.team3imple.barosiksa.domain.ingredients.service;

import com.team3imple.barosiksa.domain.ingredients.dto.request.IngredientCreateRequest;
import com.team3imple.barosiksa.domain.ingredients.dto.response.IngredientResponse;
import com.team3imple.barosiksa.domain.ingredients.entity.Ingredient;
import com.team3imple.barosiksa.domain.ingredients.repository.IngredientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class IngredientService {
    private final IngredientRepository ingredientRepository;

    @Transactional
    public Long createIngredient(IngredientCreateRequest request) {
        Ingredient ingredient = Ingredient.builder()
                .name(request.name())
                .isAllergenic(request.isAllergenic())
                .build();

        return ingredientRepository.save(ingredient).getId();
    }

    public List<IngredientResponse> searchIngredients(String keyword) {
        return ingredientRepository.searchIngredients(keyword).stream()
                .map(IngredientResponse::from)
                .toList();
    }
}
