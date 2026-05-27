package com.team3imple.barosiksa.domain.reservations.dto.response;

import com.team3imple.barosiksa.domain.reservations.entity.ReservationStatus;

import java.time.LocalDateTime;
import java.util.List;

public record OwnerReservationResponse(
        Long reservationId,
        Long memberId, // 누가 예약했는지 식별하기 위한 손님의 ID
        LocalDateTime reservationTime,
        ReservationStatus status,
        Integer totalPrice,
        List<ReservationItemDetail> items
) {
    public record ReservationItemDetail(
            Long menuId,
            String menuName,
            Integer quantity,
            Integer orderPrice
    ) {
    }
}