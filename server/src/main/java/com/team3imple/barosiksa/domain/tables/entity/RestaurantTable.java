package com.team3imple.barosiksa.domain.tables.entity;

import com.team3imple.barosiksa.domain.restaurants.entity.Restaurant;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@jakarta.persistence.Table(name = "restaurant_tables")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RestaurantTable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "table_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id", nullable = false)
    private Restaurant restaurant;

    @Column(nullable = false, length = 20)
    private String tableNumber;  // "A1", "A2", "B1" 등 테이블 번호

    @Column(nullable = false)
    private Integer capacity;    // 수용 인원

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TableStatus status;

    @Builder
    public RestaurantTable(Restaurant restaurant, String tableNumber, Integer capacity) {
        this.restaurant = restaurant;
        this.tableNumber = tableNumber;
        this.capacity = capacity;
        this.status = TableStatus.AVAILABLE;
    }

    public void updateStatus(TableStatus status) {
        this.status = status;
    }
}
