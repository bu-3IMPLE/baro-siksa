package com.team3imple.barosiksa.domain.restaurant_ingredients.entity;

import com.team3imple.barosiksa.domain.ingredients.entity.Ingredient;
import com.team3imple.barosiksa.domain.restaurants.entity.Restaurant;
import com.team3imple.barosiksa.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "restaurant_ingredients")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RestaurantIngredient extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "restaurant_ingredient_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id", nullable = false)
    private Restaurant restaurant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ingredient_id", nullable = false)
    private Ingredient ingredient;

    @Column(length = 50)
    private String origin;

    @Column(name = "stock_quantity", nullable = false)
    private Integer stockQuantity;

    @Builder
    public RestaurantIngredient(Restaurant restaurant, Ingredient ingredient, String origin, Integer stockQuantity) {
        this.restaurant = restaurant;
        this.ingredient = ingredient;
        this.origin = origin;
        this.stockQuantity = stockQuantity != null ? stockQuantity : 0;
    }

    public void updateRestaurantIngredientInfo(String origin, Integer stockQuantity) {
        this.origin = origin;
        this.stockQuantity = stockQuantity;
    }
}
