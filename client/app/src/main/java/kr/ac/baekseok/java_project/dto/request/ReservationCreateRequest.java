package kr.ac.baekseok.java_project.dto.request;

import java.util.List;

public class ReservationCreateRequest {
    public Long tableId;
    public String reservationTime;
    public List<ReservationMenuItemRequest> items;

    public ReservationCreateRequest(Long tableId, String reservationTime,
                                    List<ReservationMenuItemRequest> items) {
        this.tableId = tableId;
        this.reservationTime = reservationTime;
        this.items = items;
    }
}
