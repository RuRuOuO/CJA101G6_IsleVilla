package com.islevilla.ching.shuttleSchedule.model;

import java.util.List;

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
	
	// 查詢指定方向（0: 去程，1: 回程）的所有班次
    List<ShuttleSchedule> findByShuttleDirection(Integer shuttleDirection);

}