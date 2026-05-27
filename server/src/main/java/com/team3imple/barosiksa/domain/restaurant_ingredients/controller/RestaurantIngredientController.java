package com.team3imple.barosiksa.domain.restaurant_ingredients.controller;

import com.team3imple.barosiksa.domain.restaurant_ingredients.dto.request.RestaurantIngredientCreateRequest;
import com.team3imple.barosiksa.domain.restaurant_ingredients.dto.request.RestaurantIngredientUpdateRequest;
import com.team3imple.barosiksa.domain.restaurant_ingredients.dto.response.RestaurantIngredientResponse;
import com.team3imple.barosiksa.domain.restaurant_ingredients.service.RestaurantIngredientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Tag(name = "Restaurant Ingredient", description = "식당 식재료 관리 API")
@RestController
@RequestMapping("/api/restaurants/{restaurantId}/ingredients")
@RequiredArgsConstructor
public class RestaurantIngredientController {

    private final RestaurantIngredientService restaurantIngredientService;

    @Operation(summary = "식당 식재료 등록", description = "해당 식당에 새로운 식재료 정보를 등록합니다. (OWNER 권한 필요)")
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

    @Operation(summary = "식당 식재료 목록 조회", description = "해당 식당에 등록된 모든 식재료 목록을 조회합니다.")
    @GetMapping
    public ResponseEntity<List<RestaurantIngredientResponse>> getRestaurantIngredients(
            @PathVariable Long restaurantId) {
        List<RestaurantIngredientResponse> responses = restaurantIngredientService.getRestaurantIngredients(restaurantId);
        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "식당 식재료 정보 수정", description = "기존에 등록된 식재료 정보를 수정합니다. (OWNER 권한 필요)")
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

    @Operation(summary = "식당 식재료 삭제", description = "해당 식재료를 삭제합니다. (OWNER 권한 필요)")
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