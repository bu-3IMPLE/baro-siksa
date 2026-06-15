package kr.ac.baekseok.java_project.dto.response;

import java.io.Serializable;

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
    public String openTime;       // "HH:mm:ss"
    public String closeTime;      // "HH:mm:ss"
    public String breakStartTime; // "HH:mm:ss" or null
    public String breakEndTime;   // "HH:mm:ss" or null
    public String closedDays;
}
