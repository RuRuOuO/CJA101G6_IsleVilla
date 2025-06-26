package com.islevilla.ching.shuttleReservationSeat.model;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface BackShuttleReservationSeatRepository
		extends JpaRepository<BackShuttleReservationSeat, BackShuttleReservationSeat.ShuttleReservationSeatId> {

	List<BackShuttleReservationSeat> findByShuttleReservationId(Integer shuttleReservationId);

	List<BackShuttleReservationSeat> findBySeat_SeatNumber(String seatNumber);

	List<BackShuttleReservationSeat> findBySeatId(Integer seatId);
}
