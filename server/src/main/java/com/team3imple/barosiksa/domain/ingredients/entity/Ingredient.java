package com.team3imple.barosiksa.domain.ingredients.entity;

import com.team3imple.barosiksa.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "ingredients")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Ingredient extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ingredient_id")
    private Long id;

    @Column(nullable = false, length = 50, unique = true)
    private String name;

    @Column(name = "is_allergenic", nullable = false)
    private boolean isAllergenic;

    @Builder
    public Ingredient(String name, boolean isAllergenic) {
        this.name = name;
        this.isAllergenic = isAllergenic;
    }
}
