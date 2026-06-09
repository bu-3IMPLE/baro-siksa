package kr.ac.baekseok.java_project.dto.request;

/**
 * 예약 상태 변경 요청 (OWNER 권한)
 * status: PENDING, CONFIRMED, CANCELED, COMPLETED
 */
public class ReservationStatusUpdateRequest {
    public String status;

    public ReservationStatusUpdateRequest(String status) {
        this.status = status;
    }
}
