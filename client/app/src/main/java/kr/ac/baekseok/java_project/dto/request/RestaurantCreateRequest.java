package kr.ac.baekseok.java_project.dto.request;

/**
 * 식당 등록 요청 (OWNER 권한)
 * category: KOREAN, JAPANESE, CHINESE, WESTERN, ASIAN, CAFE, ETC
 *
 * 시간 필드는 "HH:mm:ss" 문자열 (예: "09:00:00").
 * 위도/경도는 서버에서 주소로 자동 조회한다.
 */
public class RestaurantCreateRequest {
    public String name;
    public String category;
    public String address;
    public String phoneNumber;
    public String description;
    public String openTime;       // "09:00:00"
    public String closeTime;      // "21:00:00"
    public String breakStartTime; // 없으면 null
    public String breakEndTime;   // 없으면 null
    public String closedDays;
}
