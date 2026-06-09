package kr.ac.baekseok.java_project.dto.request;

/** 음식 취향 수정 */
public class MemberUpdateRequest {
    public String foodPreference;

    public MemberUpdateRequest(String foodPreference) {
        this.foodPreference = foodPreference;
    }
}
