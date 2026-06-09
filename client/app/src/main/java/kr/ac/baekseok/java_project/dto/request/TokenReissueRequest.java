package kr.ac.baekseok.java_project.dto.request;

public class TokenReissueRequest {
    public String refreshToken;

    public TokenReissueRequest(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}
