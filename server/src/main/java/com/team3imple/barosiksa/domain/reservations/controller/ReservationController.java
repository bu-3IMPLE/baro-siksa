package com.team3imple.barosiksa.domain.reservations.controller;

import com.team3imple.barosiksa.domain.reservations.dto.request.ReservationCreateRequest;
import com.team3imple.barosiksa.domain.reservations.dto.request.ReservationStatusUpdateRequest;
import com.team3imple.barosiksa.domain.reservations.dto.request.ReservationUpdateRequest;
import com.team3imple.barosiksa.domain.reservations.dto.response.OwnerReservationResponse;
import com.team3imple.barosiksa.domain.reservations.dto.response.ReservationResponse;
import com.team3imple.barosiksa.domain.reservations.service.ReservationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/restaurants/{restaurantId}/reservations")
@RequiredArgsConstructor
public class ReservationController {
    private final ReservationService reservationService;

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

    @GetMapping("/me")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<ReservationResponse>> getMyReservations(Principal principal) {
        Long memberId = Long.valueOf(principal.getName());
        List<ReservationResponse> responses = reservationService.getMyReservations(memberId);
        return ResponseEntity.ok(responses);
    }

    @GetMapping
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<List<OwnerReservationResponse>> getReservationsForOwner(
            Principal principal,
            @PathVariable Long restaurantId) {

        Long memberId = Long.valueOf(principal.getName());
        List<OwnerReservationResponse> responses = reservationService.getReservationsForOwner(memberId, restaurantId);

        return ResponseEntity.ok(responses);
    }

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
