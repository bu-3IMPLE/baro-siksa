package com.team3imple.barosiksa.domain.reservations.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.team3imple.barosiksa.domain.menus.entity.QMenu;
import com.team3imple.barosiksa.domain.reservation_items.entity.QReservationItem;
import com.team3imple.barosiksa.domain.reservations.dto.response.OwnerReservationResponse;
import com.team3imple.barosiksa.domain.reservations.dto.response.ReservationResponse;
import com.team3imple.barosiksa.domain.reservations.dto.response.ReservationResponse.ReservationItemDetail;
import com.team3imple.barosiksa.domain.reservations.entity.QReservation;
import com.team3imple.barosiksa.domain.restaurants.entity.QRestaurant;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static com.querydsl.core.group.GroupBy.*;

@RequiredArgsConstructor
public class ReservationRepositoryCustomImpl implements ReservationRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    @Override
    public List<ReservationResponse> findMyReservations(Long memberId) {
        QReservation reservation = QReservation.reservation;
        QRestaurant restaurant = QRestaurant.restaurant;
        QReservationItem reservationItem = QReservationItem.reservationItem;
        QMenu menu = QMenu.menu;

        return queryFactory
                .from(reservation)
                .join(reservation.restaurant, restaurant)
                .leftJoin(reservationItem).on(reservationItem.reservation.eq(reservation))
                .leftJoin(reservationItem.menu, menu)
                .where(reservation.member.id.eq(memberId))
                .orderBy(reservation.createdAt.desc()) // 최근 예약한 순서대로 정렬
                .transform(
                        groupBy(reservation.id).list(
                                Projections.constructor(ReservationResponse.class,
                                        reservation.id,
                                        restaurant.id,
                                        restaurant.name,
                                        reservation.reservationTime,
                                        reservation.status,
                                        reservation.totalPrice,
                                        list(Projections.constructor(ReservationItemDetail.class,
                                                menu.id,
                                                menu.name,
                                                reservationItem.quantity,
                                                reservationItem.orderedPrice
                                        ).skipNulls())
                                )
                        )
                );
    }

    @Override
    public List<OwnerReservationResponse> findReservationsByRestaurantId(Long restaurantId) {
        QReservation reservation = QReservation.reservation;
        QReservationItem reservationItem = QReservationItem.reservationItem;
        QMenu menu = QMenu.menu;

        return queryFactory
                .from(reservation)
                .leftJoin(reservationItem).on(reservationItem.reservation.eq(reservation))
                .leftJoin(reservationItem.menu, menu)
                .where(reservation.restaurant.id.eq(restaurantId))
                .orderBy(reservation.reservationTime.asc()) // 다가오는 예약 시간 순으로 정렬
                .transform(
                        groupBy(reservation.id).list(
                                Projections.constructor(OwnerReservationResponse.class,
                                        reservation.id,
                                        reservation.member.id,
                                        reservation.reservationTime,
                                        reservation.status,
                                        reservation.totalPrice,
                                        list(Projections.constructor(OwnerReservationResponse.ReservationItemDetail.class,
                                                menu.id,
                                                menu.name,
                                                reservationItem.quantity,
                                                reservationItem.orderedPrice
                                        ).skipNulls())
                                )
                        )
                );
    }
}
