package com.team3imple.barosiksa.domain.menus.entity;

import com.team3imple.barosiksa.domain.restaurant_ingredients.entity.RestaurantIngredient;
import com.team3imple.barosiksa.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "menu_ingredients")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MenuIngredient extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "menu_ingredient_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_id", nullable = false)
    private Menu menu;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_ingredient_id", nullable = false)
    private RestaurantIngredient restaurantIngredient;

    @Builder
    public MenuIngredient(Menu menu, RestaurantIngredient restaurantIngredient) {
        this.menu = menu;
        this.restaurantIngredient = restaurantIngredient;
    }
}
