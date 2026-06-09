package com.team3imple.barosiksa.domain.menus.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QMenuIngredient is a Querydsl query type for MenuIngredient
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QMenuIngredient extends EntityPathBase<MenuIngredient> {

    private static final long serialVersionUID = 377622192L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QMenuIngredient menuIngredient = new QMenuIngredient("menuIngredient");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final QMenu menu;

    public final com.team3imple.barosiksa.domain.restaurant_ingredients.entity.QRestaurantIngredient restaurantIngredient;

    public QMenuIngredient(String variable) {
        this(MenuIngredient.class, forVariable(variable), INITS);
    }

    public QMenuIngredient(Path<? extends MenuIngredient> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QMenuIngredient(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QMenuIngredient(PathMetadata metadata, PathInits inits) {
        this(MenuIngredient.class, metadata, inits);
    }

    public QMenuIngredient(Class<? extends MenuIngredient> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.menu = inits.isInitialized("menu") ? new QMenu(forProperty("menu"), inits.get("menu")) : null;
        this.restaurantIngredient = inits.isInitialized("restaurantIngredient") ? new com.team3imple.barosiksa.domain.restaurant_ingredients.entity.QRestaurantIngredient(forProperty("restaurantIngredient"), inits.get("restaurantIngredient")) : null;
    }

}

