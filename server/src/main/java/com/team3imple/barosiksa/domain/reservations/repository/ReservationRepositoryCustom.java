package com.team3imple.barosiksa.domain.reservations.repository;

import com.team3imple.barosiksa.domain.reservations.dto.response.OwnerReservationResponse;
import com.team3imple.barosiksa.domain.reservations.dto.response.ReservationResponse;

import java.util.List;

public interface ReservationRepositoryCustom {
    List<ReservationResponse> findMyReservations(Long memberId);

    List<OwnerReservationResponse> findReservationsByRestaurantId(Long restaurantId);
}
