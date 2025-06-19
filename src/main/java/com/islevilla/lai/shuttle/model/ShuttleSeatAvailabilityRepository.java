package com.islevilla.lai.shuttle.model;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShuttleSeatAvailabilityRepository
		extends JpaRepository<ShuttleSeatAvailability, ShuttleSeatAvailability.ShuttleSeatAvailabilityId> {

	// 根據班次ID查詢座位可用性
	List<ShuttleSeatAvailability> findByShuttleScheduleId(Integer shuttleScheduleId);

	// 根據日期查詢座位可用性
	List<ShuttleSeatAvailability> findByShuttleDate(LocalDate shuttleDate);

	// 根據班次ID和日期查詢座位可用性
	Optional<ShuttleSeatAvailability> findByShuttleScheduleIdAndShuttleDate(Integer shuttleScheduleId,
			LocalDate shuttleDate);

}
