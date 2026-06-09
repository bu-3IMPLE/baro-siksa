package kr.ac.baekseok.java_project.dto.request;

public class MemberLoginRequest {
    public String email;
    public String password;

    public MemberLoginRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }
}
