package com.team3imple.barosiksa.global.util;

import com.team3imple.barosiksa.domain.member.entity.Role;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtProvider {

    private final String secretKeyString = "barosiksaSecretKeyForJwtTokenAuthentication2026";
    private final SecretKey secretKey = Keys.hmacShaKeyFor(secretKeyString.getBytes(StandardCharsets.UTF_8));

    // 만료 시간 설정 (Access: 30분, Refresh: 7일)
    private final long accessTokenValidity = 30 * 60 * 1000L;
    private final long refreshTokenValidity = 7 * 24 * 60 * 60 * 1000L;

    public String createAccessToken(Long memberId, String email, String role) {
        return createToken(memberId, email, role, accessTokenValidity);
    }

    public String createRefreshToken(Long memberId, String email, String role) {
        return createToken(memberId, email, role, refreshTokenValidity);
    }

    private String createToken(Long memberId, String email, String role, long validityInMilliseconds) {
        Date now = new Date();
        Date validity = new Date(now.getTime() + validityInMilliseconds);

        return Jwts.builder()
                .subject(String.valueOf(memberId))
                .claim("email", email)
                .claim("role", role)
                .issuedAt(now)
                .expiration(validity)
                .signWith(secretKey)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String getSubject(String token) {
        try {
            return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().getSubject();
        } catch (ExpiredJwtException e) {
            return e.getClaims().getSubject();
        }
    }

    public long getRefreshTokenValidityInSeconds() {
        return refreshTokenValidity / 1000;
    }

    public String getRole(String token) {
        try {
            return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("role", String.class);
        } catch (ExpiredJwtException e) {
            return e.getClaims().get("role", String.class);
        }
    }
}