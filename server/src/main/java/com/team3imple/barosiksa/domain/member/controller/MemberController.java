package com.team3imple.barosiksa.domain.member.controller;

import com.team3imple.barosiksa.domain.member.dto.request.*;
import com.team3imple.barosiksa.domain.member.dto.response.MemberLoginResponse;
import com.team3imple.barosiksa.domain.member.dto.response.MemberResponse;
import com.team3imple.barosiksa.domain.member.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;

    @PostMapping("/signup")
    public ResponseEntity<Long> signUp(@Valid @RequestBody MemberSignUpRequest request) {
        Long memberId = memberService.signUp(request);
        return ResponseEntity.ok(memberId);
    }

    @PostMapping("/login")
    public ResponseEntity<MemberLoginResponse> login(@Valid @RequestBody MemberLoginRequest request) {
        MemberLoginResponse response = memberService.login(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/reissue")
    public ResponseEntity<MemberLoginResponse> reissue(@Valid @RequestBody TokenReissueRequest request) {
        MemberLoginResponse response = memberService.reissue(request);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{memberId}/preference")
    public ResponseEntity<Void> updatePreference(
            @PathVariable Long memberId,
            @RequestBody MemberUpdateRequest request) {
        memberService.updateFoodPreference(memberId, request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/me")
    public ResponseEntity<MemberResponse> getMyInfo(Principal principal) {
        Long memberId = Long.valueOf(principal.getName());
        MemberResponse response = memberService.getMemberInfo(memberId);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/me/password")
    public ResponseEntity<Void> changePassword(
            Principal principal,
            @Valid @RequestBody MemberPasswordUpdateRequest request) {

        Long memberId = Long.valueOf(principal.getName());
        memberService.changePassword(memberId, request);

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/me")
    public ResponseEntity<Void> withdraw(
            Principal principal,
            @Valid @RequestBody MemberWithdrawRequest request) {

        Long memberId = Long.valueOf(principal.getName());
        memberService.withdraw(memberId, request);

        return ResponseEntity.ok().build();
    }
}