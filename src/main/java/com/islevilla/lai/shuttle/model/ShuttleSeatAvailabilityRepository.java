package com.islevilla.lai.shuttle.model;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface ShuttleSeatAvailabilityRepository
		extends JpaRepository<ShuttleSeatAvailability, ShuttleSeatAvailability.ShuttleSeatAvailabilityId> {

	// 根據班次ID和日期查詢座位可用性
	Optional<ShuttleSeatAvailability> findByShuttleScheduleIdAndShuttleDate(Integer shuttleScheduleId,
			LocalDate shuttleDate);

	// 根據班次編號和日期更新座位數量
	@Modifying
	@Transactional
	@Query("UPDATE ShuttleSeatAvailability s SET s.seatQuantity = :seatQuantity WHERE s.shuttleScheduleId = :shuttleScheduleId AND s.shuttleDate = :shuttleDate")
	int updateSeatQuantityByShuttleScheduleIdAndShuttleDate(@Param("shuttleScheduleId") Integer shuttleScheduleId,
			@Param("shuttleDate") LocalDate shuttleDate, @Param("seatQuantity") Integer seatQuantity);
}
