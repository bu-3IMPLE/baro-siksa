package com.team3imple.barosiksa.domain.reservations.dto.response;

import com.team3imple.barosiksa.domain.reservations.entity.ReservationStatus;

import java.time.LocalDateTime;
import java.util.List;

public record ReservationResponse(
        Long reservationId,
        Long restaurantId,
        String restaurantName,
        LocalDateTime reservationTime,
        ReservationStatus status,
        Integer totalPrice,
        List<ReservationItemDetail> items
) {
    // 예약에 포함된 개별 메뉴 상세 정보 내부 record
    public record ReservationItemDetail(
            Long menuId,
            String menuName,
            Integer quantity,
            Integer orderPrice
    ) {
    }
}