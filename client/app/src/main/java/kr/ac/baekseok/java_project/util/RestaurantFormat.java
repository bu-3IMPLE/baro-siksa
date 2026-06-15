package kr.ac.baekseok.java_project.util;

/**
 * 식당 정보 표시용 헬퍼.
 */
public class RestaurantFormat {

    /** 영업시간을 "09:30 ~ 21:00" 형식으로. 서버 "HH:mm:ss" 문자열을 받는다. */
    public static String openHours(String open, String close) {
        if (open == null && close == null) return "영업시간 정보 없음";
        return fmt(open) + " ~ " + fmt(close);
    }

    /** "HH:mm:ss" → "HH:mm" */
    private static String fmt(String t) {
        if (t == null) return "-";
        String[] p = t.split(":");
        return p.length >= 2 ? p[0] + ":" + p[1] : t;
    }

    /** 카테고리 영문 코드 → 한글 */
    public static String categoryKo(String category) {
        if (category == null) return "";
        switch (category) {
            case "KOREAN":   return "한식";
            case "JAPANESE": return "일식";
            case "CHINESE":  return "중식";
            case "WESTERN":  return "양식";
            case "ASIAN":    return "아시안";
            case "CAFE":     return "카페";
            case "ETC":      return "기타";
            default:         return category;
        }
    }
}
