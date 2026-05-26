package com.team3imple.barosiksa.domain.restaurant_ingredients.controller;

import com.team3imple.barosiksa.domain.restaurant_ingredients.dto.request.RestaurantIngredientCreateRequest;
import com.team3imple.barosiksa.domain.restaurant_ingredients.dto.request.RestaurantIngredientUpdateRequest;
import com.team3imple.barosiksa.domain.restaurant_ingredients.dto.response.RestaurantIngredientResponse;
import com.team3imple.barosiksa.domain.restaurant_ingredients.service.RestaurantIngredientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/restaurants/{restaurantId}/ingredients")
@RequiredArgsConstructor
public class RestaurantIngredientController {

    private final RestaurantIngredientService restaurantIngredientService;

    @PostMapping
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<Long> createRestaurantIngredient(
            Principal principal,
            @PathVariable Long restaurantId,
            @Valid @RequestBody RestaurantIngredientCreateRequest request) {

        Long memberId = Long.valueOf(principal.getName());
        Long restaurantIngredientId = restaurantIngredientService.createRestaurantIngredient(memberId, restaurantId, request);

        return ResponseEntity.ok(restaurantIngredientId);
    }

    @GetMapping
    public ResponseEntity<List<RestaurantIngredientResponse>> getRestaurantIngredients(
            @PathVariable Long restaurantId) {
        List<RestaurantIngredientResponse> responses = restaurantIngredientService.getRestaurantIngredients(restaurantId);
        return ResponseEntity.ok(responses);
    }

    @PutMapping("/{restaurantIngredientId}")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<Void> updateRestaurantIngredient(
            Principal principal,
            @PathVariable Long restaurantId,
            @PathVariable Long restaurantIngredientId,
            @Valid @RequestBody RestaurantIngredientUpdateRequest request) {

        Long memberId = Long.valueOf(principal.getName());
        restaurantIngredientService.updateRestaurantIngredient(memberId, restaurantId, restaurantIngredientId, request);

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{restaurantIngredientId}")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<Void> deleteRestaurantIngredient(
            Principal principal,
            @PathVariable Long restaurantId,
            @PathVariable Long restaurantIngredientId) {

        Long memberId = Long.valueOf(principal.getName());
        restaurantIngredientService.deleteRestaurantIngredient(memberId, restaurantId, restaurantIngredientId);

        return ResponseEntity.ok().build();
    }
}