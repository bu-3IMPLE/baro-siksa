package kr.ac.baekseok.java_project.dto.response;

import java.io.Serializable;
import java.util.List;

/** 식당 예약 목록 응답 (OWNER) */
public class OwnerReservationResponse implements Serializable {
    public long reservationId;
    public long memberId;
    public String reservationTime;
    public String status;
    public int totalPrice;
    public List<ReservationItemDetail> items;
}
