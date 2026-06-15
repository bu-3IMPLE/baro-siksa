package com.team3imple.barosiksa.domain.tables.controller;

import com.team3imple.barosiksa.domain.tables.dto.TableCreateRequest;
import com.team3imple.barosiksa.domain.tables.dto.TableResponse;
import com.team3imple.barosiksa.domain.tables.dto.TableStatusUpdateRequest;
import com.team3imple.barosiksa.domain.tables.service.TableService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Tag(name = "Table", description = "식당 테이블 관리 API")
@RestController
@RequestMapping("/api/restaurants/{restaurantId}/tables")
@RequiredArgsConstructor
public class TableController {

    private final TableService tableService;

    @Operation(summary = "테이블 목록 조회 (실시간 상태 포함)")
    @GetMapping
    public ResponseEntity<List<TableResponse>> getTables(@PathVariable Long restaurantId) {
        return ResponseEntity.ok(tableService.getTables(restaurantId));
    }

    @Operation(summary = "테이블 추가 (업주 전용)")
    @PostMapping
    public ResponseEntity<Long> addTable(
            Principal principal,
            @PathVariable Long restaurantId,
            @Valid @RequestBody TableCreateRequest request) {
        Long memberId = Long.valueOf(principal.getName());
        return ResponseEntity.ok(tableService.addTable(memberId, restaurantId, request));
    }

    @Operation(summary = "테이블 상태 변경 (업주/관리자 전용) - AVAILABLE 또는 OCCUPIED만 허용")
    @PatchMapping("/{tableId}/status")
    @PreAuthorize("hasAnyRole('OWNER','ADMIN')")
    public ResponseEntity<Void> updateTableStatus(
            Principal principal,
            @PathVariable Long restaurantId,
            @PathVariable Long tableId,
            @Valid @RequestBody TableStatusUpdateRequest request) {
        Long memberId = Long.valueOf(principal.getName());
        tableService.updateTableStatus(memberId, restaurantId, tableId, request);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "테이블 삭제 (업주 전용)")
    @DeleteMapping("/{tableId}")
    public ResponseEntity<Void> deleteTable(
            Principal principal,
            @PathVariable Long restaurantId,
            @PathVariable Long tableId) {
        Long memberId = Long.valueOf(principal.getName());
        tableService.deleteTable(memberId, restaurantId, tableId);
        return ResponseEntity.ok().build();
    }
}
