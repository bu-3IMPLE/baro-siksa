package com.team3imple.barosiksa.domain.ingredients.controller;

import com.team3imple.barosiksa.domain.ingredients.dto.request.IngredientCreateRequest;
import com.team3imple.barosiksa.domain.ingredients.dto.response.IngredientResponse;
import com.team3imple.barosiksa.domain.ingredients.service.IngredientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "마스터 재료 (Ingredient)", description = "시스템 전역에서 공통으로 사용되는 마스터 재료 데이터 관리 API")
@RestController
@RequestMapping("/api/ingredients")
@RequiredArgsConstructor
public class IngredientController {
    private final IngredientService ingredientService;

    @Operation(summary = "마스터 재료 생성", description = "시스템에 새로운 마스터 재료를 등록합니다. 재료의 이름과 알레르기 유발 여부를 입력받아 저장합니다.")
    @PostMapping
    public ResponseEntity<Long> createIngredient(@Valid @RequestBody IngredientCreateRequest request) {
        Long ingredientId = ingredientService.createIngredient(request);
        return ResponseEntity.ok(ingredientId);
    }

    @Operation(summary = "마스터 재료 조회 및 검색", description = "전체 마스터 재료 목록을 조회합니다. keyword 파라미터를 전달하면 해당 키워드가 이름에 포함된 재료만 필터링하여 반환합니다.")
    @GetMapping
    public ResponseEntity<List<IngredientResponse>> searchIngredients(
            @RequestParam(required = false) String keyword) {

        List<IngredientResponse> ingredients = ingredientService.searchIngredients(keyword);
        return ResponseEntity.ok(ingredients);
    }
}