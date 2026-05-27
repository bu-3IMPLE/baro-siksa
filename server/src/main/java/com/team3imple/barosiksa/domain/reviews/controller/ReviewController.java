package com.team3imple.barosiksa.domain.reviews.controller;

import com.team3imple.barosiksa.domain.reviews.dto.request.ReviewCreateRequest;
import com.team3imple.barosiksa.domain.reviews.dto.request.ReviewUpdateRequest;
import com.team3imple.barosiksa.domain.reviews.dto.response.ReviewResponse;
import com.team3imple.barosiksa.domain.reviews.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Tag(name = "Review", description = "식당 리뷰 관리 API")
@RestController
@RequestMapping("/api/restaurants/{restaurantId}/reviews")
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;

    @Operation(summary = "리뷰 등록", description = "해당 식당에 새로운 리뷰를 등록합니다. (USER 권한 필요)")
    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Long> createReview(
            Principal principal,
            @PathVariable Long restaurantId,
            @Valid @RequestBody ReviewCreateRequest request) {

        Long memberId = Long.valueOf(principal.getName());
        Long reviewId = reviewService.createReview(memberId, restaurantId, request);

        return ResponseEntity.ok(reviewId);
    }

    @Operation(summary = "식당 리뷰 목록 조회", description = "특정 식당의 모든 리뷰를 최신순으로 조회합니다.")
    @GetMapping
    public ResponseEntity<List<ReviewResponse>> getRestaurantReviews(
            @PathVariable Long restaurantId) {

        List<ReviewResponse> responses = reviewService.getRestaurantReviews(restaurantId);
        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "리뷰 수정", description = "기존에 작성한 리뷰를 수정합니다. (작성자 본인만 가능)")
    @PatchMapping("/{reviewId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Void> updateReview(
            Principal principal,
            @PathVariable Long restaurantId,
            @PathVariable Long reviewId,
            @Valid @RequestBody ReviewUpdateRequest request) {

        Long memberId = Long.valueOf(principal.getName());
        reviewService.updateReview(memberId, restaurantId, reviewId, request);

        return ResponseEntity.ok().build();
    }

    @Operation(summary = "리뷰 삭제", description = "기존에 작성한 리뷰를 삭제합니다. (작성자 본인만 가능)")
    @DeleteMapping("/{reviewId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Void> deleteReview(
            Principal principal,
            @PathVariable Long restaurantId,
            @PathVariable Long reviewId) {

        Long memberId = Long.valueOf(principal.getName());
        reviewService.deleteReview(memberId, restaurantId, reviewId);

        return ResponseEntity.ok().build();
    }
}
