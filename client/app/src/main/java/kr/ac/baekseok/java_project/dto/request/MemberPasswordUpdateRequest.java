package kr.ac.baekseok.java_project.dto.request;

/** 비밀번호 변경. newPassword는 영문+숫자+특수문자 8~20자 */
public class MemberPasswordUpdateRequest {
    public String currentPassword;
    public String newPassword;

    public MemberPasswordUpdateRequest(String currentPassword, String newPassword) {
        this.currentPassword = currentPassword;
        this.newPassword = newPassword;
    }
}
