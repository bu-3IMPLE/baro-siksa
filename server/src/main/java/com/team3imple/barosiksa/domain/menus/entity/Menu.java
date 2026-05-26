package com.team3imple.barosiksa.domain.menus.entity;

import com.team3imple.barosiksa.domain.restaurants.entity.Restaurant;
import com.team3imple.barosiksa.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "menus")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Menu extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "menu_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id", nullable = false)
    private Restaurant restaurant;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false)
    private Integer price;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Builder
    public Menu(Restaurant restaurant, String name, Integer price, String description) {
        this.restaurant = restaurant;
        this.name = name;
        this.price = price;
        this.description = description;
    }

    public void updateMenuInfo(String name, Integer price, String description) {
        this.name = name;
        this.price = price;
        this.description = description;
    }
}
