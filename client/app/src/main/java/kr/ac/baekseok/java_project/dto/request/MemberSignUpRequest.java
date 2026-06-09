package kr.ac.baekseok.java_project.dto.request;

/**
 * 회원가입 요청
 * 비밀번호 규칙: 영문 + 숫자 + 특수문자(@$!%*#?&) 포함 8~20자
 * role: "USER", "OWNER", "ADMIN"
 */
public class MemberSignUpRequest {
    public String username;
    public String email;
    public String password;
    public String role;

    public MemberSignUpRequest(String username, String email, String password, String role) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.role = role;
    }
}
