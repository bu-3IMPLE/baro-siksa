package com.team3imple.barosiksa.domain.reservations.repository;

import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.team3imple.barosiksa.domain.menus.entity.QMenu;
import com.team3imple.barosiksa.domain.reservation_items.entity.QReservationItem;
import com.team3imple.barosiksa.domain.reservations.dto.response.OwnerReservationResponse;
import com.team3imple.barosiksa.domain.reservations.dto.response.ReservationResponse;
import com.team3imple.barosiksa.domain.reservations.dto.response.ReservationResponse.ReservationItemDetail;
import com.team3imple.barosiksa.domain.reservations.entity.QReservation;
import com.team3imple.barosiksa.domain.restaurants.entity.QRestaurant;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class ReservationRepositoryCustomImpl implements ReservationRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    @Override
    public List<ReservationResponse> findMyReservations(Long memberId) {
        QReservation reservation = QReservation.reservation;
        QRestaurant restaurant = QRestaurant.restaurant;
        QReservationItem reservationItem = QReservationItem.reservationItem;
        QMenu menu = QMenu.menu;

        List<Tuple> rows = queryFactory
                .select(reservation.id, restaurant.id, restaurant.name,
                        reservation.reservationTime, reservation.status, reservation.totalPrice,
                        menu.id, menu.name, reservationItem.quantity, reservationItem.orderedPrice)
                .from(reservation)
                .join(reservation.restaurant, restaurant)
                .leftJoin(reservationItem).on(reservationItem.reservation.eq(reservation))
                .leftJoin(reservationItem.menu, menu)
                .where(reservation.member.id.eq(memberId))
                .orderBy(reservation.createdAt.desc())
                .fetch();

        Map<Long, ReservationResponse> map = new LinkedHashMap<>();
        for (Tuple row : rows) {
            Long rId = row.get(reservation.id);
            Long menuId = row.get(menu.id);

            ReservationItemDetail item = menuId != null
                    ? new ReservationItemDetail(menuId, row.get(menu.name),
                            row.get(reservationItem.quantity), row.get(reservationItem.orderedPrice))
                    : null;

            if (!map.containsKey(rId)) {
                List<ReservationItemDetail> items = new ArrayList<>();
                if (item != null) items.add(item);
                map.put(rId, new ReservationResponse(rId, row.get(restaurant.id),
                        row.get(restaurant.name), row.get(reservation.reservationTime),
                        row.get(reservation.status), row.get(reservation.totalPrice), items));
            } else if (item != null) {
                map.get(rId).items().add(item);
            }
        }
        return new ArrayList<>(map.values());
    }

    @Override
    public List<OwnerReservationResponse> findReservationsByRestaurantId(Long restaurantId) {
        QReservation reservation = QReservation.reservation;
        QReservationItem reservationItem = QReservationItem.reservationItem;
        QMenu menu = QMenu.menu;

        List<Tuple> rows = queryFactory
                .select(reservation.id, reservation.member.id,
                        reservation.reservationTime, reservation.status, reservation.totalPrice,
                        menu.id, menu.name, reservationItem.quantity, reservationItem.orderedPrice)
                .from(reservation)
                .leftJoin(reservationItem).on(reservationItem.reservation.eq(reservation))
                .leftJoin(reservationItem.menu, menu)
                .where(reservation.restaurant.id.eq(restaurantId))
                .orderBy(reservation.reservationTime.asc())
                .fetch();

        Map<Long, OwnerReservationResponse> map = new LinkedHashMap<>();
        for (Tuple row : rows) {
            Long rId = row.get(reservation.id);
            Long menuId = row.get(menu.id);

            OwnerReservationResponse.ReservationItemDetail item = menuId != null
                    ? new OwnerReservationResponse.ReservationItemDetail(
                            menuId, row.get(menu.name),
                            row.get(reservationItem.quantity), row.get(reservationItem.orderedPrice))
                    : null;

            if (!map.containsKey(rId)) {
                List<OwnerReservationResponse.ReservationItemDetail> items = new ArrayList<>();
                if (item != null) items.add(item);
                map.put(rId, new OwnerReservationResponse(rId, row.get(reservation.member.id),
                        row.get(reservation.reservationTime), row.get(reservation.status),
                        row.get(reservation.totalPrice), items));
            } else if (item != null) {
                map.get(rId).items().add(item);
            }
        }
        return new ArrayList<>(map.values());
    }
}
