package kr.ac.baekseok.java_project.dto.request;

import java.io.Serializable;

/** 예약에 포함되는 메뉴 1줄 (메뉴ID + 수량) */
public class ReservationMenuItemRequest implements Serializable {
    public long menuId;
    public int quantity;

    public ReservationMenuItemRequest(long menuId, int quantity) {
        this.menuId = menuId;
        this.quantity = quantity;
    }
}
