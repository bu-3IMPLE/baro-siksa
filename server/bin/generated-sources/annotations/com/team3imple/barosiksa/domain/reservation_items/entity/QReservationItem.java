package com.team3imple.barosiksa.domain.reservation_items.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QReservationItem is a Querydsl query type for ReservationItem
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QReservationItem extends EntityPathBase<ReservationItem> {

    private static final long serialVersionUID = -486662490L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QReservationItem reservationItem = new QReservationItem("reservationItem");

    public final com.team3imple.barosiksa.global.common.QBaseTimeEntity _super = new com.team3imple.barosiksa.global.common.QBaseTimeEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final com.team3imple.barosiksa.domain.menus.entity.QMenu menu;

    public final NumberPath<Integer> orderedPrice = createNumber("orderedPrice", Integer.class);

    public final NumberPath<Integer> quantity = createNumber("quantity", Integer.class);

    public final com.team3imple.barosiksa.domain.reservations.entity.QReservation reservation;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public QReservationItem(String variable) {
        this(ReservationItem.class, forVariable(variable), INITS);
    }

    public QReservationItem(Path<? extends ReservationItem> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QReservationItem(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QReservationItem(PathMetadata metadata, PathInits inits) {
        this(ReservationItem.class, metadata, inits);
    }

    public QReservationItem(Class<? extends ReservationItem> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.menu = inits.isInitialized("menu") ? new com.team3imple.barosiksa.domain.menus.entity.QMenu(forProperty("menu"), inits.get("menu")) : null;
        this.reservation = inits.isInitialized("reservation") ? new com.team3imple.barosiksa.domain.reservations.entity.QReservation(forProperty("reservation"), inits.get("reservation")) : null;
    }

}

