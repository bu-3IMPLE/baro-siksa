package com.team3imple.barosiksa.domain.member.controller;

import com.team3imple.barosiksa.domain.member.dto.request.*;
import com.team3imple.barosiksa.domain.member.dto.response.MemberLoginResponse;
import com.team3imple.barosiksa.domain.member.dto.response.MemberResponse;
import com.team3imple.barosiksa.domain.member.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Tag(name = "Member", description = "회원 가입, 로그인, 마이페이지 등 회원 관련 API")
@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;

    @Operation(summary = "회원가입", description = "새로운 회원을 등록합니다.")
    @PostMapping("/signup")
    public ResponseEntity<Long> signUp(@Valid @RequestBody MemberSignUpRequest request) {
        Long memberId = memberService.signUp(request);
        return ResponseEntity.ok(memberId);
    }

    @Operation(summary = "로그인", description = "회원 로그인을 진행하고 JWT 토큰을 발급합니다.")
    @PostMapping("/login")
    public ResponseEntity<MemberLoginResponse> login(@Valid @RequestBody MemberLoginRequest request) {
        MemberLoginResponse response = memberService.login(request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "토큰 재발급", description = "Refresh Token을 사용하여 새로운 Access Token을 발급합니다.")
    @PostMapping("/reissue")
    public ResponseEntity<MemberLoginResponse> reissue(@Valid @RequestBody TokenReissueRequest request) {
        MemberLoginResponse response = memberService.reissue(request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "음식 취향 수정", description = "회원의 음식 취향 정보를 업데이트합니다.")
    @PatchMapping("/{memberId}/preference")
    public ResponseEntity<Void> updatePreference(
            @PathVariable Long memberId,
            @RequestBody MemberUpdateRequest request) {
        memberService.updateFoodPreference(memberId, request);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "내 정보 조회", description = "현재 로그인한 회원의 정보를 조회합니다.")
    @GetMapping("/me")
    public ResponseEntity<MemberResponse> getMyInfo(Principal principal) {
        Long memberId = Long.valueOf(principal.getName());
        MemberResponse response = memberService.getMemberInfo(memberId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "닉네임 변경", description = "현재 로그인한 회원의 닉네임을 변경합니다.")
    @PatchMapping("/me/nickname")
    public ResponseEntity<Void> updateNickname(
            Principal principal,
            @Valid @RequestBody MemberNicknameUpdateRequest request) {
        Long memberId = Long.valueOf(principal.getName());
        memberService.updateNickname(memberId, request);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "비밀번호 변경", description = "현재 로그인한 회원의 비밀번호를 변경합니다.")
    @PatchMapping("/me/password")
    public ResponseEntity<Void> changePassword(
            Principal principal,
            @Valid @RequestBody MemberPasswordUpdateRequest request) {

        Long memberId = Long.valueOf(principal.getName());
        memberService.changePassword(memberId, request);

        return ResponseEntity.ok().build();
    }

    @Operation(summary = "회원 탈퇴", description = "현재 로그인한 회원을 탈퇴 처리합니다.")
    @DeleteMapping("/me")
    public ResponseEntity<Void> withdraw(
            Principal principal,
            @Valid @RequestBody MemberWithdrawRequest request) {

        Long memberId = Long.valueOf(principal.getName());
        memberService.withdraw(memberId, request);

        return ResponseEntity.ok().build();
    }
}