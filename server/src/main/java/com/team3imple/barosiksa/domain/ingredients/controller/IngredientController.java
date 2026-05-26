package com.team3imple.barosiksa.domain.ingredients.controller;

import com.team3imple.barosiksa.domain.ingredients.dto.request.IngredientCreateRequest;
import com.team3imple.barosiksa.domain.ingredients.dto.response.IngredientResponse;
import com.team3imple.barosiksa.domain.ingredients.service.IngredientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ingredients")
@RequiredArgsConstructor
public class IngredientController {
    private final IngredientService ingredientService;

    @PostMapping
    public ResponseEntity<Long> createIngredient(@Valid @RequestBody IngredientCreateRequest request) {
        Long ingredientId = ingredientService.createIngredient(request);
        return ResponseEntity.ok(ingredientId);
    }

    @GetMapping
    public ResponseEntity<List<IngredientResponse>> searchIngredients(
            @RequestParam(required = false) String keyword) {

        List<IngredientResponse> ingredients = ingredientService.searchIngredients(keyword);
        return ResponseEntity.ok(ingredients);
    }
}