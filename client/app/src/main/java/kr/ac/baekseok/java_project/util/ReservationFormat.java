package kr.ac.baekseok.java_project.util;

/**
 * 예약 상태/표시 변환 헬퍼.
 */
public class ReservationFormat {

    /** 상태 코드 → 한글 */
    public static String statusKo(String status) {
        if (status == null) return "";
        switch (status) {
            case "PENDING":   return "대기중";
            case "CONFIRMED": return "확정";
            case "CANCELED":  return "취소됨";
            case "COMPLETED": return "완료";
            default:          return status;
        }
    }

    /** 상태 색상 (텍스트 컬러용 hex) */
    public static int statusColor(String status) {
        if (status == null) return 0xFF666666;
        switch (status) {
            case "PENDING":   return 0xFFF57C00; // 주황
            case "CONFIRMED": return 0xFF2E7D32; // 초록
            case "CANCELED":  return 0xFFD32F2F; // 빨강
            case "COMPLETED": return 0xFF1565C0; // 파랑
            default:          return 0xFF666666;
        }
    }
}
