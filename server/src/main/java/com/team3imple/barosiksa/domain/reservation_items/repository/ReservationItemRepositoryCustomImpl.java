package com.team3imple.barosiksa.domain.reservation_items.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.team3imple.barosiksa.domain.reservation_items.entity.QReservationItem;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ReservationItemRepositoryCustomImpl implements ReservationItemRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    @Override
    public void deleteByReservationId(Long reservationId) {
        QReservationItem reservationItem = QReservationItem.reservationItem;

        queryFactory.delete(reservationItem)
                .where(reservationItem.reservation.id.eq(reservationId))
                .execute();
    }
}
