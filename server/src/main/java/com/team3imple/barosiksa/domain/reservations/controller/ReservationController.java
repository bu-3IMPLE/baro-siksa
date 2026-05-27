package com.team3imple.barosiksa.domain.reservations.controller;

import com.team3imple.barosiksa.domain.reservations.dto.request.ReservationCreateRequest;
import com.team3imple.barosiksa.domain.reservations.dto.request.ReservationStatusUpdateRequest;
import com.team3imple.barosiksa.domain.reservations.dto.request.ReservationUpdateRequest;
import com.team3imple.barosiksa.domain.reservations.dto.response.OwnerReservationResponse;
import com.team3imple.barosiksa.domain.reservations.dto.response.ReservationResponse;
import com.team3imple.barosiksa.domain.reservations.service.ReservationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Tag(name = "Reservation", description = "예약 관리 API")
@RestController
@RequestMapping("/api/restaurants/{restaurantId}/reservations")
@RequiredArgsConstructor
public class ReservationController {
    private final ReservationService reservationService;

    @Operation(summary = "예약 생성", description = "식당에 새로운 예약을 생성합니다. (USER 권한 필요)")
    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Long> createReservation(
            Principal principal,
            @PathVariable Long restaurantId,
            @Valid @RequestBody ReservationCreateRequest request) {

        Long memberId = Long.valueOf(principal.getName());
        Long reservationId = reservationService.createReservation(memberId, restaurantId, request);

        return ResponseEntity.ok(reservationId);
    }

    @Operation(summary = "내 예약 목록 조회", description = "현재 로그인한 사용자의 예약 목록을 조회합니다. (USER 권한 필요)")
    @GetMapping("/me")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<ReservationResponse>> getMyReservations(Principal principal) {
        Long memberId = Long.valueOf(principal.getName());
        List<ReservationResponse> responses = reservationService.getMyReservations(memberId);
        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "식당 예약 목록 조회", description = "해당 식당의 예약 목록을 조회합니다. (OWNER 권한 필요)")
    @GetMapping
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<List<OwnerReservationResponse>> getReservationsForOwner(
            Principal principal,
            @PathVariable Long restaurantId) {

        Long memberId = Long.valueOf(principal.getName());
        List<OwnerReservationResponse> responses = reservationService.getReservationsForOwner(memberId, restaurantId);

        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "예약 상태 변경", description = "예약의 상태(예: 승인, 거절 등)를 변경합니다. (OWNER 권한 필요)")
    @PatchMapping("/{reservationId}/status")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<Void> updateReservationStatus(
            Principal principal,
            @PathVariable Long restaurantId,
            @PathVariable Long reservationId,
            @Valid @RequestBody ReservationStatusUpdateRequest request) {

        Long memberId = Long.valueOf(principal.getName());
        reservationService.updateReservationStatus(memberId, restaurantId, reservationId, request);

        return ResponseEntity.ok().build();
    }

    @Operation(summary = "예약 정보 수정", description = "기존 예약 정보를 수정합니다. (USER 권한 필요)")
    @PutMapping("/{reservationId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Void> updateReservation(
            Principal principal,
            @PathVariable Long restaurantId,
            @PathVariable Long reservationId,
            @Valid @RequestBody ReservationUpdateRequest request) {

        Long memberId = Long.valueOf(principal.getName());
        reservationService.updateReservation(memberId, restaurantId, reservationId, request);

        return ResponseEntity.ok().build();
    }

    @Operation(summary = "예약 취소", description = "예약을 취소(삭제)합니다. (USER 권한 필요)")
    @DeleteMapping("/{reservationId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Void> deleteReservation(
            Principal principal,
            @PathVariable Long restaurantId,
            @PathVariable Long reservationId) {

        Long memberId = Long.valueOf(principal.getName());
        reservationService.deleteReservation(memberId, restaurantId, reservationId);

        return ResponseEntity.ok().build();
    }
}
