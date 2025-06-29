package com.islevilla.lai.shuttle.model;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

	// 根據日期範圍查詢座位可用性
	List<ShuttleSeatAvailability> findByShuttleDateBetween(LocalDate startDate, LocalDate endDate);

	// 根據班次ID和日期範圍查詢座位可用性
	List<ShuttleSeatAvailability> findByShuttleScheduleIdAndShuttleDateBetween(Integer shuttleScheduleId,
			LocalDate startDate, LocalDate endDate);

	// 根據班次ID和日期檢查是否存在
	boolean existsByShuttleScheduleIdAndShuttleDate(Integer shuttleScheduleId, LocalDate shuttleDate);

	// 刪除特定日期的所有座位設定
	void deleteByShuttleDate(LocalDate shuttleDate);

	// 刪除指定班次ID的所有座位設定
	void deleteByShuttleScheduleId(Integer shuttleScheduleId);

	// 刪除指定班次ID和日期的記錄
	void deleteByShuttleScheduleIdAndShuttleDate(Integer shuttleScheduleId, LocalDate shuttleDate);

	// 根據座位數量範圍查詢
	// 查詢座位數量大於指定值的記錄
	List<ShuttleSeatAvailability> findBySeatQuantityGreaterThan(Integer seatQuantity);

	// 查詢座位數量大於等於指定值的記錄
	List<ShuttleSeatAvailability> findBySeatQuantityGreaterThanEqual(Integer minSeats);

	// 查詢特定日期座位數量大於指定值的記錄
	List<ShuttleSeatAvailability> findByShuttleDateAndSeatQuantityGreaterThanEqual(LocalDate shuttleDate,
			Integer seatQuantity);

	// 根據班次ID和最小座位數查詢
	List<ShuttleSeatAvailability> findByShuttleScheduleIdAndSeatQuantityGreaterThanEqual(Integer shuttleScheduleId,
			Integer minSeats);

	// 自定義查詢：查找指定日期範圍內座位充足的班次
	@Query("SELECT s FROM ShuttleSeatAvailability s WHERE s.shuttleDate BETWEEN :startDate AND :endDate AND s.seatQuantity >= :minSeats")
	List<ShuttleSeatAvailability> findAvailableSeatsInDateRange(@Param("startDate") LocalDate startDate,
			@Param("endDate") LocalDate endDate, @Param("minSeats") Integer minSeats);

	// 自定義查詢：統計指定班次在日期範圍內的總座位數
	@Query("SELECT SUM(s.seatQuantity) FROM ShuttleSeatAvailability s WHERE s.shuttleScheduleId = :scheduleId AND s.shuttleDate BETWEEN :startDate AND :endDate")
	Long getTotalAvailableSeatsForScheduleInDateRange(@Param("scheduleId") Integer scheduleId,
			@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

	// 查詢特定班次和日期的可用座位數（扣除已預約數量）
	@Query("SELECT (ssa.seatQuantity - COALESCE(" + "(SELECT SUM(sr.shuttleNumber) FROM ShuttleReservation sr "
			+ "WHERE sr.shuttleSchedule.shuttleScheduleId = :scheduleId " + "AND sr.shuttleDate = :date "
			+ "AND sr.shuttleDirection = :direction " + "AND sr.shuttleReservationStatus = 1), 0)) AS availableSeats "
			+ "FROM ShuttleSeatAvailability ssa "
			+ "WHERE ssa.shuttleScheduleId = :scheduleId AND ssa.shuttleDate = :date")
	Integer getAvailableSeats(@Param("scheduleId") Integer scheduleId, @Param("date") LocalDate date,
			@Param("direction") Integer direction);

}
