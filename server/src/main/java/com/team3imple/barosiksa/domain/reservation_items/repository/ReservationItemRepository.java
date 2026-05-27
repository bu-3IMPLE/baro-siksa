package com.team3imple.barosiksa.domain.reservation_items.repository;

import com.team3imple.barosiksa.domain.reservation_items.entity.ReservationItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationItemRepository extends JpaRepository<ReservationItem, Long>, ReservationItemRepositoryCustom {
}
