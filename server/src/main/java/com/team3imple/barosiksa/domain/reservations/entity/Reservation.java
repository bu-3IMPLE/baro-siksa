package com.team3imple.barosiksa.domain.reservations.entity;

import com.team3imple.barosiksa.domain.member.entity.Member;
import com.team3imple.barosiksa.domain.restaurants.entity.Restaurant;
import com.team3imple.barosiksa.domain.tables.entity.RestaurantTable;
import com.team3imple.barosiksa.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "reservations")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Reservation extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reservation_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id", nullable = false)
    private Restaurant restaurant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "table_id")
    private RestaurantTable table;

    @Column(nullable = false)
    private LocalDateTime reservationTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ReservationStatus status;

    @Column(nullable = false)
    private Integer totalPrice;

    @Builder
    public Reservation(Member member, Restaurant restaurant, RestaurantTable table,
                       LocalDateTime reservationTime, ReservationStatus status, Integer totalPrice) {
        this.member = member;
        this.restaurant = restaurant;
        this.table = table;
        this.reservationTime = reservationTime;
        this.status = status;
        this.totalPrice = totalPrice;
    }

    public void updateStatus(ReservationStatus status) {
        this.status = status;
    }

    public void updateReservationInfo(LocalDateTime reservationTime, Integer totalPrice) {
        this.reservationTime = reservationTime;
        this.totalPrice = totalPrice;
    }
}
