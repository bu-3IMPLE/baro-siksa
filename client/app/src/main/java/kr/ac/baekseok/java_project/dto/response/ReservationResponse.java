package kr.ac.baekseok.java_project.dto.response;

import java.io.Serializable;
import java.util.List;

/**
 * 내 예약 목록 응답 (USER)
 * status: PENDING, CONFIRMED, CANCELED, COMPLETED
 */
public class ReservationResponse implements Serializable {
    public long reservationId;
    public long restaurantId;
    public String restaurantName;
    public String reservationTime;   // ISO-8601 문자열
    public String status;
    public int totalPrice;
    public List<ReservationItemDetail> items;
}
