package kr.ac.baekseok.java_project.dto.response;

import java.io.Serializable;

/** 예약에 포함된 메뉴 상세 */
public class ReservationItemDetail implements Serializable {
    public long menuId;
    public String menuName;
    public int quantity;
    public int orderPrice;
}
