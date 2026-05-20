package com.team3imple.barosiksa.domain.member.service;

import com.team3imple.barosiksa.domain.member.dto.request.*;
import com.team3imple.barosiksa.domain.member.dto.response.MemberLoginResponse;
import com.team3imple.barosiksa.domain.member.dto.response.MemberResponse;
import com.team3imple.barosiksa.domain.member.entity.Member;
import com.team3imple.barosiksa.domain.member.repository.MemberRepository;
import com.team3imple.barosiksa.global.error.CustomException;
import com.team3imple.barosiksa.global.error.ErrorCode;
import com.team3imple.barosiksa.global.util.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final StringRedisTemplate redisTemplate;

    @Transactional
    public Long signUp(MemberSignUpRequest request) {
        if (memberRepository.findByEmail(request.email()).isPresent()) {
            throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);
        }

        Member member = Member.builder()
                .username(request.username())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .role(request.role())
                .build();

        return memberRepository.save(member).getId();
    }

    @Transactional
    public void updateFoodPreference(Long memberId, MemberUpdateRequest request) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        member.updateFoodPreference(request.foodPreference());
    }

    public MemberLoginResponse login(MemberLoginRequest request) {
        Member member = memberRepository.findByEmail(request.email())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        if (member.isDeleted() || !passwordEncoder.matches(request.password(), member.getPassword())) {
            throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);
        }

        return generateAndSaveTokens(member);
    }

    @Transactional
    public MemberLoginResponse reissue(TokenReissueRequest request) {
        String refreshToken = request.refreshToken();

        if (!jwtProvider.validateToken(refreshToken)) {
            throw new CustomException(ErrorCode.INVALID_INPUT_VALUE); // 유효하지 않은 토큰
        }

        Long memberId = Long.valueOf(jwtProvider.getSubject(refreshToken));

        String savedRefreshToken = redisTemplate.opsForValue().get("RT:" + memberId);
        if (savedRefreshToken == null || !savedRefreshToken.equals(refreshToken)) {
            throw new CustomException(ErrorCode.INVALID_INPUT_VALUE); // 토큰 불일치 혹은 만료
        }

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        return generateAndSaveTokens(member);
    }


    public MemberResponse getMemberInfo(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        if (member.isDeleted()) {
            throw new CustomException(ErrorCode.USER_NOT_FOUND);
        }

        return MemberResponse.from(member);
    }

    @Transactional
    public void changePassword(Long memberId, MemberPasswordUpdateRequest request) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        if (!passwordEncoder.matches(request.currentPassword(), member.getPassword())) {
            throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);
        }

        member.updatePassword(passwordEncoder.encode(request.newPassword()));
    }

    @Transactional
    public void withdraw(Long memberId, MemberWithdrawRequest request) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        if (member.isDeleted()) {
            throw new CustomException(ErrorCode.USER_NOT_FOUND);
        }

        if (!passwordEncoder.matches(request.password(), member.getPassword())) {
            throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);
        }

        member.withdraw();

        redisTemplate.delete("RT:" + memberId);
    }

    private MemberLoginResponse generateAndSaveTokens(Member member) {
        String accessToken = jwtProvider.createAccessToken(member.getId(), member.getEmail(), member.getRole().name());
        String refreshToken = jwtProvider.createRefreshToken(member.getId(), member.getEmail(), member.getRole().name());

        redisTemplate.opsForValue().set(
                "RT:" + member.getId(),
                refreshToken,
                jwtProvider.getRefreshTokenValidityInSeconds(),
                TimeUnit.SECONDS
        );

        return new MemberLoginResponse(accessToken, refreshToken);
    }
}