package com.team3imple.barosiksa.domain.restaurants.controller;

import com.team3imple.barosiksa.domain.restaurants.dto.request.RestaurantCreateRequest;
import com.team3imple.barosiksa.domain.restaurants.dto.request.RestaurantSearchRequest;
import com.team3imple.barosiksa.domain.restaurants.dto.request.RestaurantUpdateRequest;
import com.team3imple.barosiksa.domain.restaurants.dto.response.RestaurantResponse;
import com.team3imple.barosiksa.domain.restaurants.dto.response.RestaurantSearchCondition;
import com.team3imple.barosiksa.domain.restaurants.service.RestaurantService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/restaurants")
@RequiredArgsConstructor
public class RestaurantController {
    private final RestaurantService restaurantService;

    @PostMapping
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<Long> createRestaurant(
            Principal principal,
            @Valid @RequestBody RestaurantCreateRequest request) {
        Long memberId = Long.parseLong(principal.getName());
        Long restaurantId = restaurantService.createRestaurant(memberId, request);

        return ResponseEntity.ok(restaurantId);
    }

    @GetMapping("/{restaurantId}")
    public ResponseEntity<RestaurantResponse> getRestaurant(@PathVariable Long restaurantId) {
        RestaurantResponse response = restaurantService.getRestaurant(restaurantId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{restaurantId}")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<Void> updateRestaurant(
            Principal principal,
            @PathVariable Long restaurantId,
            @Valid @RequestBody RestaurantUpdateRequest request) {

        Long memberId = Long.valueOf(principal.getName());
        restaurantService.updateRestaurant(memberId, restaurantId, request);

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{restaurantId}")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<Void> deleteRestaurant(
            Principal principal,
            @PathVariable Long restaurantId) {

        Long memberId = Long.valueOf(principal.getName());
        restaurantService.deleteRestaurant(memberId, restaurantId);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/nearby")
    public ResponseEntity<List<RestaurantResponse>> getNearbyRestaurants(
            @Valid RestaurantSearchRequest request) {

        List<RestaurantResponse> responses = restaurantService.searchNearbyRestaurants(request);
        return ResponseEntity.ok(responses);
    }

    @GetMapping
    public ResponseEntity<Page<RestaurantResponse>> searchRestaurants(
            RestaurantSearchCondition condition,
            @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<RestaurantResponse> responses = restaurantService.searchRestaurants(condition, pageable);
        return ResponseEntity.ok(responses);
    }
}
