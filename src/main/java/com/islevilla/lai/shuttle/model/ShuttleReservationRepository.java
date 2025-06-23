package com.islevilla.lai.shuttle.model;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.islevilla.lai.members.model.Members;
import com.islevilla.wei.room.model.RoomRVOrder;

@Repository
public interface ShuttleReservationRepository extends JpaRepository<ShuttleReservation, Integer> {

	// 根據會員ID查詢預約記錄
	List<ShuttleReservation> findByMembers(Members member);

	// 根據訂房ID查詢預約記錄
	List<ShuttleReservation> findByRoomRVOrder(RoomRVOrder roomRVOrder);

	// 根據接駁日期查詢預約記錄
	List<ShuttleReservation> findByShuttleDate(LocalDate shuttleDate);

//	// 根據班次ID和日期查詢預約記錄
//	List<ShuttleReservation> findByShuttleScheduleIdAndShuttleDate(Integer shuttleScheduleId, LocalDate shuttleDate);

	// 根據班次和日期查詢預約記錄
	List<ShuttleReservation> findByShuttleScheduleAndShuttleDate(ShuttleSchedule shuttleSchedule,
			LocalDate shuttleDate);

	// 根據班次、日期和方向查詢預約記錄
	List<ShuttleReservation> findByShuttleScheduleAndShuttleDateAndShuttleDirection(ShuttleSchedule shuttleSchedule,
			LocalDate shuttleDate, Integer shuttleDirection);

	// 根據預約狀態查詢
	List<ShuttleReservation> findByShuttleReservationStatus(Integer status);

	// 查詢特定日期和班次的預約人數總和
	@Query("SELECT COALESCE(SUM(sr.shuttleNumber), 0) FROM ShuttleReservation sr "
			+ "WHERE sr.shuttleSchedule.shuttleScheduleId = :scheduleId AND sr.shuttleDate = :date AND sr.shuttleReservationStatus = 1")
	Integer getTotalReservedSeats(@Param("scheduleId") Integer scheduleId, @Param("date") LocalDate date);

	// 查詢特定日期、班次和方向的預約人數總和
	@Query("SELECT COALESCE(SUM(sr.shuttleNumber), 0) FROM ShuttleReservation sr "
			+ "WHERE sr.shuttleSchedule.shuttleScheduleId = :scheduleId AND sr.shuttleDate = :date "
			+ "AND sr.shuttleDirection = :direction AND sr.shuttleReservationStatus = 1")
	Integer getTotalReservedSeatsByDirection(@Param("scheduleId") Integer scheduleId, @Param("date") LocalDate date,
			@Param("direction") Integer direction);

	// 查詢特定日期和班次的預約數量
	@Query("SELECT COUNT(sr) FROM ShuttleReservation sr WHERE sr.shuttleDate = :shuttleDate "
			+ "AND sr.shuttleSchedule = :shuttleSchedule AND sr.shuttleReservationStatus = 1")
	Long countReservationsByDateAndSchedule(@Param("shuttleDate") LocalDate shuttleDate,
			@Param("shuttleSchedule") ShuttleSchedule shuttleSchedule);
	
	// 查詢特定日期和班次的預約數量
//	@Query("SELECT COUNT(sr) FROM ShuttleReservation sr WHERE sr.shuttleDate = :shuttleDate "
//			+ "AND sr.shuttleScheduleId = :scheduleId AND sr.shuttleReservationStatus = 1")
//	Long countReservationsByDateAndSchedule(@Param("shuttleDate") LocalDate shuttleDate,
//			@Param("scheduleId") Integer scheduleId);
	
	// 查詢特定日期和班次ID的預約數量（使用班次ID）  
	@Query("SELECT COUNT(sr) FROM ShuttleReservation sr WHERE sr.shuttleDate = :shuttleDate "
			+ "AND sr.shuttleSchedule.shuttleScheduleId = :scheduleId AND sr.shuttleReservationStatus = 1")
	Long countReservationsByDateAndScheduleId(@Param("shuttleDate") LocalDate shuttleDate,
			@Param("scheduleId") Integer scheduleId);

	// 查詢會員在特定日期的預約
	List<ShuttleReservation> findByMembersAndShuttleDateAndShuttleReservationStatus(Members member,
			LocalDate shuttleDate, Integer status);

	// 查詢會員在特定日期、班次和方向的預約
	List<ShuttleReservation> findByMembersAndShuttleDateAndShuttleScheduleAndShuttleDirection(Members member,
			LocalDate shuttleDate, ShuttleSchedule shuttleSchedule, Integer shuttleDirection);

	// 根據訂房編號和預約狀態查詢
	List<ShuttleReservation> findByRoomRVOrderAndShuttleReservationStatus(RoomRVOrder roomRVOrder, Integer status);

	// 查詢特定日期範圍內的預約
	List<ShuttleReservation> findByShuttleDateBetween(LocalDate startDate, LocalDate endDate);

	// 查詢特定日期範圍內指定狀態的預約
	List<ShuttleReservation> findByShuttleDateBetweenAndShuttleReservationStatus(LocalDate startDate, LocalDate endDate,
			Integer status);

	

}
