package kr.ac.baekseok.java_project.dto.request;

/**
 * 식당 등록 요청 (OWNER 권한)
 * category: KOREAN, JAPANESE, CHINESE, WESTERN, ASIAN, CAFE, ETC
 *
 * 시간 필드는 "HH:mm:ss" 문자열 (예: "09:00:00").
 * 서버 LocalTime 역직렬화가 문자열을 기대하므로 객체가 아닌 문자열로 보낸다.
 */
public class RestaurantCreateRequest {
    public String name;
    public String category;
    public String address;
    public double latitude;
    public double longitude;
    public String phoneNumber;
    public String description;
    public String openTime;       // "09:00:00"
    public String closeTime;      // "21:00:00"
    public String breakStartTime; // 없으면 null
    public String breakEndTime;   // 없으면 null
    public String closedDays;
}
