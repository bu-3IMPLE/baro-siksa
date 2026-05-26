package com.team3imple.barosiksa.domain.reservations.service;

import com.team3imple.barosiksa.domain.member.entity.Member;
import com.team3imple.barosiksa.domain.member.repository.MemberRepository;
import com.team3imple.barosiksa.domain.menus.entity.Menu;
import com.team3imple.barosiksa.domain.menus.repository.MenuRepository;
import com.team3imple.barosiksa.domain.reservation_items.entity.ReservationItem;
import com.team3imple.barosiksa.domain.reservation_items.repository.ReservationItemRepository;
import com.team3imple.barosiksa.domain.reservations.dto.request.ReservationCreateRequest;
import com.team3imple.barosiksa.domain.reservations.dto.request.ReservationMenuItemRequest;
import com.team3imple.barosiksa.domain.reservations.dto.request.ReservationStatusUpdateRequest;
import com.team3imple.barosiksa.domain.reservations.dto.request.ReservationUpdateRequest;
import com.team3imple.barosiksa.domain.reservations.dto.response.OwnerReservationResponse;
import com.team3imple.barosiksa.domain.reservations.dto.response.ReservationResponse;
import com.team3imple.barosiksa.domain.reservations.entity.Reservation;
import com.team3imple.barosiksa.domain.reservations.entity.ReservationStatus;
import com.team3imple.barosiksa.domain.reservations.repository.ReservationRepository;
import com.team3imple.barosiksa.domain.restaurants.entity.Restaurant;
import com.team3imple.barosiksa.domain.restaurants.repository.RestaurantRepository;
import com.team3imple.barosiksa.global.error.CustomException;
import com.team3imple.barosiksa.global.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReservationService {
    private final ReservationRepository reservationRepository;
    private final ReservationItemRepository reservationItemRepository;
    private final MemberRepository memberRepository;
    private final RestaurantRepository restaurantRepository;
    private final MenuRepository menuRepository;

    @Transactional
    public Long createReservation(Long memberId, Long restaurantId, ReservationCreateRequest request) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_INPUT_VALUE));

        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_INPUT_VALUE));

        List<Long> menuIds = request.items().stream()
                .map(ReservationMenuItemRequest::menuId)
                .toList();
        List<Menu> menus = menuRepository.findAllById(menuIds);

        if (menus.size() != menuIds.size()) {
            throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);
        }

        Map<Long, Menu> menuMap = menus.stream().collect(Collectors.toMap(Menu::getId, menu -> menu));
        int totalPrice = 0;

        for (ReservationMenuItemRequest item : request.items()) {
            Menu menu = menuMap.get(item.menuId());
            if (!menu.getRestaurant().getId().equals(restaurantId)) {
                throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);
            }
            totalPrice += menu.getPrice() * item.quantity();
        }

        Reservation reservation = Reservation.builder()
                .member(member)
                .restaurant(restaurant)
                .reservationTime(request.reservationTime())
                .status(ReservationStatus.PENDING)
                .totalPrice(totalPrice)
                .build();

        Reservation savedReservation = reservationRepository.save(reservation);

        for (ReservationMenuItemRequest item : request.items()) {
            Menu menu = menuMap.get(item.menuId());
            ReservationItem reservationItem = ReservationItem.builder()
                    .reservation(savedReservation)
                    .menu(menu)
                    .quantity(item.quantity())
                    .orderPrice(menu.getPrice()) // 예약 당시 가격 스냅샷
                    .build();

            reservationItemRepository.save(reservationItem);
        }

        return savedReservation.getId();
    }

    public List<ReservationResponse> getMyReservations(Long memberId) {
        if (!memberRepository.existsById(memberId)) {
            throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);
        }
        return reservationRepository.findMyReservations(memberId);
    }

    public List<OwnerReservationResponse> getReservationsForOwner(Long memberId, Long restaurantId) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_INPUT_VALUE));

        if (!restaurant.getMember().getId().equals(memberId)) {
            throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);
        }

        return reservationRepository.findReservationsByRestaurantId(restaurantId);
    }

    @Transactional
    public void updateReservationStatus(Long memberId, Long restaurantId, Long reservationId, ReservationStatusUpdateRequest request) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_INPUT_VALUE));

        if (!reservation.getRestaurant().getId().equals(restaurantId) ||
                !reservation.getRestaurant().getMember().getId().equals(memberId)) {
            throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);
        }

        reservation.updateStatus(request.status());
    }

    @Transactional
    public void updateReservation(Long memberId, Long restaurantId, Long reservationId, ReservationUpdateRequest request) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_INPUT_VALUE));

        if (!reservation.getMember().getId().equals(memberId) ||
                !reservation.getRestaurant().getId().equals(restaurantId)) {
            throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);
        }

        if (reservation.getStatus() != ReservationStatus.PENDING) {
            throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);
        }

        List<Long> menuIds = request.items().stream()
                .map(ReservationMenuItemRequest::menuId)
                .toList();
        List<Menu> menus = menuRepository.findAllById(menuIds);

        if (menus.size() != menuIds.size()) {
            throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);
        }

        Map<Long, Menu> menuMap = menus.stream().collect(Collectors.toMap(Menu::getId, menu -> menu));
        int newTotalPrice = 0;

        for (ReservationMenuItemRequest item : request.items()) {
            Menu menu = menuMap.get(item.menuId());
            if (!menu.getRestaurant().getId().equals(restaurantId)) {
                throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);
            }
            newTotalPrice += menu.getPrice() * item.quantity();
        }

        reservation.updateReservationInfo(request.reservationTime(), newTotalPrice);

        reservationItemRepository.deleteByReservationId(reservationId);

        for (ReservationMenuItemRequest item : request.items()) {
            Menu menu = menuMap.get(item.menuId());
            ReservationItem reservationItem = ReservationItem.builder()
                    .reservation(reservation)
                    .menu(menu)
                    .quantity(item.quantity())
                    .orderPrice(menu.getPrice())
                    .build();

            reservationItemRepository.save(reservationItem);
        }
    }

    @Transactional
    public void deleteReservation(Long memberId, Long restaurantId, Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_INPUT_VALUE));

        if (!reservation.getMember().getId().equals(memberId) ||
                !reservation.getRestaurant().getId().equals(restaurantId)) {
            throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);
        }

        if (reservation.getStatus() != ReservationStatus.PENDING &&
                reservation.getStatus() != ReservationStatus.CANCELED) {
            throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);
        }

        reservationItemRepository.deleteByReservationId(reservationId);

        reservationRepository.delete(reservation);
    }
}
