package com.islevilla.lai.shuttle.model;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ShuttleReservationRepository extends JpaRepository<ShuttleReservation, Integer> {

	// 根據會員ID查詢預約記錄
	List<ShuttleReservation> findByMemberId(Integer memberId);

	// 根據訂房ID查詢預約記錄
	List<ShuttleReservation> findByRoomReservationId(Integer roomReservationId);

	// 根據接駁日期查詢預約記錄
	List<ShuttleReservation> findByShuttleDate(LocalDate shuttleDate);

	// 根據班次ID和日期查詢預約記錄
	List<ShuttleReservation> findByShuttleScheduleIdAndShuttleDate(Integer shuttleScheduleId, LocalDate shuttleDate);

	// 根據預約狀態查詢
	List<ShuttleReservation> findByShuttleReservationStatus(Integer status);

	// 查詢特定日期和班次的預約人數總和
	@Query("SELECT COALESCE(SUM(sr.shuttleNumber), 0) FROM ShuttleReservation sr "
			+ "WHERE sr.shuttleScheduleId = :scheduleId AND sr.shuttleDate = :date AND sr.shuttleReservationStatus = 1")
	Integer getTotalReservedSeats(@Param("scheduleId") Integer scheduleId, @Param("date") LocalDate date);
	
	// 查詢特定日期和班次的預約數量
    @Query("SELECT COUNT(sr) FROM ShuttleReservation sr WHERE sr.shuttleDate = :shuttleDate " +
           "AND sr.shuttleScheduleId = :scheduleId AND sr.shuttleReservationStatus = 1")
    Long countReservationsByDateAndSchedule(@Param("shuttleDate") LocalDate shuttleDate, 
                                          @Param("scheduleId") Integer scheduleId);
    
    // 查詢會員在特定日期的預約
    List<ShuttleReservation> findByMemberIdAndShuttleDateAndShuttleReservationStatus(
        Integer memberId, LocalDate shuttleDate, Integer status);

}
