package com.team3imple.barosiksa.domain.menus.controller;

import com.team3imple.barosiksa.domain.menus.dto.request.MenuCreateRequest;
import com.team3imple.barosiksa.domain.menus.dto.request.MenuUpdateRequest;
import com.team3imple.barosiksa.domain.menus.dto.response.MenuResponse;
import com.team3imple.barosiksa.domain.menus.service.MenuService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/restaurants/{restaurantId}/menus")
@RequiredArgsConstructor
public class MenuController {
    private final MenuService menuService;

    @PostMapping
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<Long> createMenu(
            Principal principal,
            @PathVariable Long restaurantId,
            @Valid @RequestBody MenuCreateRequest request) {

        Long memberId = Long.valueOf(principal.getName());
        Long menuId = menuService.createMenu(memberId, restaurantId, request);

        return ResponseEntity.ok(menuId);
    }

    @GetMapping
    public ResponseEntity<List<MenuResponse>> getMenusByRestaurantId(
            Principal principal,
            @PathVariable Long restaurantId) {
        Long memberId = (principal != null) ? Long.valueOf(principal.getName()) : null;

        List<MenuResponse> responses = menuService.getMenusByRestaurantId(restaurantId, memberId);
        return ResponseEntity.ok(responses);
    }

    @PutMapping("/{menuId}")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<Void> updateMenu(
            Principal principal,
            @PathVariable Long restaurantId,
            @PathVariable Long menuId,
            @Valid @RequestBody MenuUpdateRequest request) {

        Long memberId = Long.valueOf(principal.getName());
        menuService.updateMenu(memberId, restaurantId, menuId, request);

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{menuId}")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<Void> deleteMenu(
            Principal principal,
            @PathVariable Long restaurantId,
            @PathVariable Long menuId) {

        Long memberId = Long.valueOf(principal.getName());
        menuService.deleteMenu(memberId, restaurantId, menuId);

        return ResponseEntity.ok().build();
    }
}
