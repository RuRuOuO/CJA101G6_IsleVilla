package com.islevilla.lai.shuttle.model;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShuttleScheduleRepository extends JpaRepository<ShuttleSchedule, Integer> {

	// 根據方向查詢班次並按出發時間排序
	List<ShuttleSchedule> findByShuttleDirectionOrderByShuttleDepartureTime(Integer shuttleDirection);

}
