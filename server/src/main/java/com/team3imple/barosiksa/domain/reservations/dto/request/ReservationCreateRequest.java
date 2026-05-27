package com.team3imple.barosiksa.domain.reservations.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.List;

public record ReservationCreateRequest(
        @NotNull(message = "예약 시간은 필수입니다.")
        LocalDateTime reservationTime,

        @NotEmpty(message = "최소 한 개 이상의 메뉴를 선택해야 합니다.")
        @Valid
        List<ReservationMenuItemRequest> items
) {
}
