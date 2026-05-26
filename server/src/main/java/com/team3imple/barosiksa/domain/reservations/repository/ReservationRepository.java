package com.team3imple.barosiksa.domain.reservations.repository;

import com.team3imple.barosiksa.domain.reservations.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationRepository extends JpaRepository<Reservation, Long>, ReservationRepositoryCustom {
}
