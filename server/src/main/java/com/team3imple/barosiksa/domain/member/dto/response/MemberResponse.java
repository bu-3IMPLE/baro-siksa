package com.team3imple.barosiksa.domain.member.dto.response;

import com.team3imple.barosiksa.domain.member.entity.Member;

public record MemberResponse(
        Long id,
        String username,
        String email,
        String foodPreference
) {
    public static MemberResponse from(Member member) {
        return new MemberResponse(
                member.getId(),
                member.getUsername(),
                member.getEmail(),
                member.getFoodPreference()
        );
    }
}
