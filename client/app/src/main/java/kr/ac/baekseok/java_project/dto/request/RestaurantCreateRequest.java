package kr.ac.baekseok.java_project.dto.request;

import kr.ac.baekseok.java_project.dto.ApiTime;

/**
 * 식당 등록 요청 (OWNER 권한)
 * category: KOREAN, JAPANESE, CHINESE, WESTERN, ASIAN, CAFE, ETC
 */
public class RestaurantCreateRequest {
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
