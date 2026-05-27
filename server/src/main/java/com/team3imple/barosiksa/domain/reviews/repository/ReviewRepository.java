package com.team3imple.barosiksa.domain.reviews.repository;

import com.team3imple.barosiksa.domain.reviews.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Long>, ReviewRepositoryCustom {
    boolean existsByReservationId(Long reservationId);
}
