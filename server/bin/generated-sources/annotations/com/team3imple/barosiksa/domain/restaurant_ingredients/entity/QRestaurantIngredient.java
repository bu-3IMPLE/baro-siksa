package com.team3imple.barosiksa.domain.restaurant_ingredients.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QRestaurantIngredient is a Querydsl query type for RestaurantIngredient
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QRestaurantIngredient extends EntityPathBase<RestaurantIngredient> {

    private static final long serialVersionUID = 80954140L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QRestaurantIngredient restaurantIngredient = new QRestaurantIngredient("restaurantIngredient");

    public final com.team3imple.barosiksa.global.common.QBaseTimeEntity _super = new com.team3imple.barosiksa.global.common.QBaseTimeEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final com.team3imple.barosiksa.domain.ingredients.entity.QIngredient ingredient;

    public final StringPath origin = createString("origin");

    public final com.team3imple.barosiksa.domain.restaurants.entity.QRestaurant restaurant;

    public final NumberPath<Integer> stockQuantity = createNumber("stockQuantity", Integer.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public QRestaurantIngredient(String variable) {
        this(RestaurantIngredient.class, forVariable(variable), INITS);
    }

    public QRestaurantIngredient(Path<? extends RestaurantIngredient> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QRestaurantIngredient(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QRestaurantIngredient(PathMetadata metadata, PathInits inits) {
        this(RestaurantIngredient.class, metadata, inits);
    }

    public QRestaurantIngredient(Class<? extends RestaurantIngredient> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.ingredient = inits.isInitialized("ingredient") ? new com.team3imple.barosiksa.domain.ingredients.entity.QIngredient(forProperty("ingredient")) : null;
        this.restaurant = inits.isInitialized("restaurant") ? new com.team3imple.barosiksa.domain.restaurants.entity.QRestaurant(forProperty("restaurant"), inits.get("restaurant")) : null;
    }

}

