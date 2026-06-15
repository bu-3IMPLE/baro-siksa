package kr.ac.baekseok.java_project.dto.response;

/** 내 정보 조회 응답 */
public class MemberResponse {
    public long id;
    public String username;
    public String email;
    public String foodPreference;
    public String role; // "USER" | "OWNER" | "ADMIN"

    public boolean isOwner() {
        return "OWNER".equals(role) || "ADMIN".equals(role);
    }
}
