package kr.ac.baekseok.java_project.dto.request;

/** 식당 정보 수정 요청 (OWNER 권한). 시간은 "HH:mm:ss" 문자열. 위도/경도는 서버에서 주소로 자동 조회한다. */
public class RestaurantUpdateRequest {
    public String name;
    public String category;
    public String address;
    public String phoneNumber;
    public String description;
    public String openTime;
    public String closeTime;
    public String breakStartTime;
    public String breakEndTime;
    public String closedDays;
}
