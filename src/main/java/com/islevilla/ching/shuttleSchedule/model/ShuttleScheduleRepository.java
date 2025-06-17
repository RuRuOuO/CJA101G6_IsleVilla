package com.islevilla.ching.shuttleSchedule.model;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ShuttleScheduleRepository extends JpaRepository<ShuttleSchedule, Integer> {

	boolean existsByDirectionAndDepartureTimeAndArrivalTime(
			Integer direction, 
			java.time.LocalTime departureTime,
			java.time.LocalTime arrivalTime);

	boolean existsByDirectionAndDepartureTimeAndArrivalTimeAndIdNot(
			Integer direction,
			java.time.LocalTime departureTime, 
			java.time.LocalTime arrivalTime, 
			Integer id);
}