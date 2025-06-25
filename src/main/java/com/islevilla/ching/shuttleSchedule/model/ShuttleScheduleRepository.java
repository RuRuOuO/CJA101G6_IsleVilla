package com.islevilla.ching.shuttleSchedule.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("chingShuttleScheduleRepository")
public interface ShuttleScheduleRepository extends JpaRepository<ShuttleSchedule, Integer> {

	boolean existsByShuttleDirectionAndShuttleDepartureTimeAndShuttleArrivalTime(
			Integer shuttleDirection, 
			java.time.LocalTime shuttleDepartureTime,
			java.time.LocalTime shuttleArrivalTime);

	boolean existsByShuttleDirectionAndShuttleDepartureTimeAndShuttleArrivalTimeAndShuttleScheduleIdNot(
			Integer shuttleDirection,
			java.time.LocalTime shuttleDepartureTime, 
			java.time.LocalTime shuttleArrivalTime, 
			Integer shuttleScheduleId);
}