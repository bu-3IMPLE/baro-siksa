package com.team3imple.barosiksa.domain.member.dto.response;

public record MemberLoginResponse(
        String accessToken,
        String refreshToken
) {
}
