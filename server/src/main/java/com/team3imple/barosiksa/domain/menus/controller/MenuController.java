package com.team3imple.barosiksa.domain.menus.controller;

import com.team3imple.barosiksa.domain.menus.dto.request.MenuCreateRequest;
import com.team3imple.barosiksa.domain.menus.dto.request.MenuUpdateRequest;
import com.team3imple.barosiksa.domain.menus.dto.response.MenuResponse;
import com.team3imple.barosiksa.domain.menus.service.MenuService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Tag(name = "Menu", description = "식당 메뉴 관리 API")
@RestController
@RequestMapping("/api/restaurants/{restaurantId}/menus")
@RequiredArgsConstructor
public class MenuController {
    private final MenuService menuService;

    @Operation(summary = "메뉴 등록", description = "특정 식당에 새로운 메뉴를 등록합니다. (OWNER 권한 필요)")
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

    @Operation(summary = "식당 메뉴 목록 조회", description = "특정 식당의 메뉴 목록을 조회합니다.")
    @GetMapping
    public ResponseEntity<List<MenuResponse>> getMenusByRestaurantId(
            Principal principal,
            @PathVariable Long restaurantId) {
        Long memberId = (principal != null) ? Long.valueOf(principal.getName()) : null;

        List<MenuResponse> responses = menuService.getMenusByRestaurantId(restaurantId, memberId);
        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "메뉴 수정", description = "기존 메뉴의 정보를 수정합니다. (OWNER 권한 필요)")
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

    @Operation(summary = "메뉴 삭제", description = "특정 메뉴를 삭제합니다. (OWNER 권한 필요)")
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
