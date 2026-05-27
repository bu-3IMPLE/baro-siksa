package com.team3imple.barosiksa.domain.reviews.dto.response;

import com.team3imple.barosiksa.domain.reviews.entity.Review;

import java.time.LocalDateTime;

public record ReviewResponse(
        Long reviewId,
        Long memberId,
        Integer rating,
        String comment,
        LocalDateTime createdAt
) {
    public ReviewResponse(Review review) {
        this(
                review.getId(),
                review.getMember().getId(),
                review.getRating(),
                review.getComment(),
                review.getCreatedAt()
        );
    }
}