package com.team3imple.barosiksa.domain.restaurants.controller;

import com.team3imple.barosiksa.domain.restaurants.dto.request.RestaurantCreateRequest;
import com.team3imple.barosiksa.domain.restaurants.dto.request.RestaurantSearchRequest;
import com.team3imple.barosiksa.domain.restaurants.dto.request.RestaurantUpdateRequest;
import com.team3imple.barosiksa.domain.restaurants.dto.response.RestaurantResponse;
import com.team3imple.barosiksa.domain.restaurants.dto.response.RestaurantSearchCondition;
import com.team3imple.barosiksa.domain.restaurants.service.RestaurantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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

@Tag(name = "Restaurant", description = "식당 정보 관리 및 검색 API")
@RestController
@RequestMapping("/api/restaurants")
@RequiredArgsConstructor
public class RestaurantController {
    private final RestaurantService restaurantService;

    @Operation(summary = "식당 등록", description = "새로운 식당을 등록합니다. (OWNER 권한 필요)")
    @PostMapping
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<Long> createRestaurant(
            Principal principal,
            @Valid @RequestBody RestaurantCreateRequest request) {
        Long memberId = Long.parseLong(principal.getName());
        Long restaurantId = restaurantService.createRestaurant(memberId, request);

        return ResponseEntity.ok(restaurantId);
    }

    @Operation(summary = "식당 단건 조회", description = "특정 식당의 상세 정보를 조회합니다.")
    @GetMapping("/{restaurantId}")
    public ResponseEntity<RestaurantResponse> getRestaurant(@PathVariable Long restaurantId) {
        RestaurantResponse response = restaurantService.getRestaurant(restaurantId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "식당 정보 수정", description = "등록된 식당의 정보를 수정합니다. (OWNER 권한 필요)")
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

    @Operation(summary = "식당 삭제", description = "등록된 식당을 삭제 처리합니다. (OWNER 권한 필요)")
    @DeleteMapping("/{restaurantId}")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<Void> deleteRestaurant(
            Principal principal,
            @PathVariable Long restaurantId) {

        Long memberId = Long.valueOf(principal.getName());
        restaurantService.deleteRestaurant(memberId, restaurantId);

        return ResponseEntity.ok().build();
    }

    @Operation(summary = "주변 식당 검색", description = "요청한 위치 기반으로 주변 식당 목록을 조회합니다.")
    @GetMapping("/nearby")
    public ResponseEntity<List<RestaurantResponse>> getNearbyRestaurants(
            @Valid RestaurantSearchRequest request) {

        List<RestaurantResponse> responses = restaurantService.searchNearbyRestaurants(request);
        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "식당 조건 검색", description = "조건에 맞는 식당 목록을 페이징하여 조회합니다.")
    @GetMapping
    public ResponseEntity<Page<RestaurantResponse>> searchRestaurants(
            RestaurantSearchCondition condition,
            @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<RestaurantResponse> responses = restaurantService.searchRestaurants(condition, pageable);
        return ResponseEntity.ok(responses);
    }
}
