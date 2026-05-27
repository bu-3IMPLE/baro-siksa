package com.team3imple.barosiksa.domain.reviews.service;

import com.team3imple.barosiksa.domain.member.entity.Member;
import com.team3imple.barosiksa.domain.member.repository.MemberRepository;
import com.team3imple.barosiksa.domain.reservations.entity.Reservation;
import com.team3imple.barosiksa.domain.reservations.repository.ReservationRepository;
import com.team3imple.barosiksa.domain.restaurants.entity.Restaurant;
import com.team3imple.barosiksa.domain.restaurants.repository.RestaurantRepository;
import com.team3imple.barosiksa.domain.reviews.dto.request.ReviewCreateRequest;
import com.team3imple.barosiksa.domain.reviews.dto.request.ReviewUpdateRequest;
import com.team3imple.barosiksa.domain.reviews.dto.response.ReviewResponse;
import com.team3imple.barosiksa.domain.reviews.entity.Review;
import com.team3imple.barosiksa.domain.reviews.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final MemberRepository memberRepository;
    private final RestaurantRepository restaurantRepository;
    private final ReservationRepository reservationRepository;

    @Transactional
    public Long createReview(Long memberId, Long restaurantId, ReviewCreateRequest request) {
        if (reviewRepository.existsByReservationId(request.reservationId())) {
            throw new IllegalStateException("이미 리뷰가 작성된 예약입니다.");
        }

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 식당입니다."));

        Reservation reservation = reservationRepository.findById(request.reservationId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 예약입니다."));

        Review review = Review.builder()
                .member(member)
                .restaurant(restaurant)
                .reservation(reservation)
                .rating(request.rating())
                .comment(request.comment())
                .build();

        return reviewRepository.save(review).getId();
    }

    @Transactional(readOnly = true)
    public List<ReviewResponse> getRestaurantReviews(Long restaurantId) {
        return reviewRepository.findByRestaurantIdOrderByIdDesc(restaurantId).stream()
                .map(ReviewResponse::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public void updateReview(Long memberId, Long restaurantId, Long reviewId, ReviewUpdateRequest request) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 리뷰입니다."));

        if (!review.getMember().getId().equals(memberId)) {
            throw new IllegalStateException("리뷰 작성자 본인만 수정할 수 있습니다.");
        }

        if (!review.getRestaurant().getId().equals(restaurantId)) {
            throw new IllegalArgumentException("해당 식당의 리뷰가 아닙니다.");
        }

        review.update(request.rating(), request.comment());
    }

    @Transactional
    public void deleteReview(Long memberId, Long restaurantId, Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 리뷰입니다."));

        if (!review.getMember().getId().equals(memberId)) {
            throw new IllegalStateException("리뷰 작성자 본인만 삭제할 수 있습니다.");
        }

        if (!review.getRestaurant().getId().equals(restaurantId)) {
            throw new IllegalArgumentException("해당 식당의 리뷰가 아닙니다.");
        }

        reviewRepository.delete(review);
    }
}
