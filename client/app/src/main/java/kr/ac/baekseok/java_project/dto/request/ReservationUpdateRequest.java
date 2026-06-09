package kr.ac.baekseok.java_project.dto.request;

import java.util.List;

/** 예약 수정 요청 (USER 권한) */
public class ReservationUpdateRequest {
    public String reservationTime;
    public List<ReservationMenuItemRequest> items;

    public ReservationUpdateRequest(String reservationTime,
                                    List<ReservationMenuItemRequest> items) {
        this.reservationTime = reservationTime;
        this.items = items;
    }
}
