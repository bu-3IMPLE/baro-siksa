package com.team3imple.barosiksa.domain.reservation_items.entity;

import com.team3imple.barosiksa.domain.menus.entity.Menu;
import com.team3imple.barosiksa.domain.reservations.entity.Reservation;
import com.team3imple.barosiksa.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "reservation_items")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReservationItem extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reservation_item_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_id", nullable = false)
    private Reservation reservation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_id", nullable = false)
    private Menu menu;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false)
    private Integer orderedPrice; // 예약 당시의 1개당 가격 스냅샷

    @Builder
    public ReservationItem(Reservation reservation, Menu menu, Integer quantity, Integer orderedPrice) {
        this.reservation = reservation;
        this.menu = menu;
        this.quantity = quantity;
        this.orderedPrice = orderedPrice;
    }
}
