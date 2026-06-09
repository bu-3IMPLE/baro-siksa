package kr.ac.baekseok.java_project.dto.request;

import java.util.List;

/**
 * 예약 생성 요청 (USER 권한)
 * reservationTime: ISO-8601 형식 문자열 (예: "2025-12-01T18:30:00")
 */
public class ReservationCreateRequest {
    public String reservationTime;
    public List<ReservationMenuItemRequest> items;

    public ReservationCreateRequest(String reservationTime,
                                    List<ReservationMenuItemRequest> items) {
        this.reservationTime = reservationTime;
        this.items = items;
    }
}
