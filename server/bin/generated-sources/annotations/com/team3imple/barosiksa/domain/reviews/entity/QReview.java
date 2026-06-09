package com.team3imple.barosiksa.domain.reviews.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QReview is a Querydsl query type for Review
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QReview extends EntityPathBase<Review> {

    private static final long serialVersionUID = -73731425L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QReview review = new QReview("review");

    public final StringPath comment = createString("comment");

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final BooleanPath isDeleted = createBoolean("isDeleted");

    public final com.team3imple.barosiksa.domain.member.entity.QMember member;

    public final NumberPath<Integer> rating = createNumber("rating", Integer.class);

    public final com.team3imple.barosiksa.domain.reservations.entity.QReservation reservation;

    public final com.team3imple.barosiksa.domain.restaurants.entity.QRestaurant restaurant;

    public final DateTimePath<java.time.LocalDateTime> updatedAt = createDateTime("updatedAt", java.time.LocalDateTime.class);

    public QReview(String variable) {
        this(Review.class, forVariable(variable), INITS);
    }

    public QReview(Path<? extends Review> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QReview(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QReview(PathMetadata metadata, PathInits inits) {
        this(Review.class, metadata, inits);
    }

    public QReview(Class<? extends Review> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.member = inits.isInitialized("member") ? new com.team3imple.barosiksa.domain.member.entity.QMember(forProperty("member")) : null;
        this.reservation = inits.isInitialized("reservation") ? new com.team3imple.barosiksa.domain.reservations.entity.QReservation(forProperty("reservation"), inits.get("reservation")) : null;
        this.restaurant = inits.isInitialized("restaurant") ? new com.team3imple.barosiksa.domain.restaurants.entity.QRestaurant(forProperty("restaurant"), inits.get("restaurant")) : null;
    }

}

