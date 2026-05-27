package com.team3imple.barosiksa.domain.reservations.dto.request;

import com.team3imple.barosiksa.domain.reservations.entity.ReservationStatus;
import jakarta.validation.constraints.NotNull;

public record ReservationStatusUpdateRequest(
        @NotNull(message = "변경할 상태 값은 필수입니다.")
        ReservationStatus status
) {
}