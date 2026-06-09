package kr.ac.baekseok.java_project.dto.request;

/** 회원 탈퇴 (비밀번호 확인) */
public class MemberWithdrawRequest {
    public String password;

    public MemberWithdrawRequest(String password) {
        this.password = password;
    }
}
