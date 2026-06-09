package kr.ac.baekseok.java_project.dto.response;

import java.io.Serializable;

import kr.ac.baekseok.java_project.dto.ApiTime;

/** 식당 상세/목록 응답 */
public class RestaurantResponse implements Serializable {
    public long id;
    public String name;
    public String category;
    public String address;
    public double latitude;
    public double longitude;
    public String phoneNumber;
    public String description;
    public ApiTime openTime;
    public ApiTime closeTime;
    public ApiTime breakStartTime;
    public ApiTime breakEndTime;
    public String closedDays;
}
