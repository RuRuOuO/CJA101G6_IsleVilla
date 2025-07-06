package com.islevilla.lai.shuttle.model;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ShuttleReservationSeatRepository
		extends JpaRepository<ShuttleReservationSeat, ShuttleReservationSeat.ShuttleReservationSeatId> {

	// 根據預約ID查詢所有座位
	List<ShuttleReservationSeat> findByShuttleReservationId(Integer shuttleReservationId);

	// 根據預約查詢所有座位
	List<ShuttleReservationSeat> findByShuttleReservation(ShuttleReservation shuttleReservation);

	// 根據座位ID查詢所有預約
	List<ShuttleReservationSeat> findBySeatId(Integer seatId);

	// 根據座位查詢所有預約
	List<ShuttleReservationSeat> findBySeat(Seat seat);

	// 檢查特定預約和座位的組合是否存在
	boolean existsByShuttleReservationIdAndSeatId(Integer shuttleReservationId, Integer seatId);

	// 刪除特定預約的所有座位
	void deleteByShuttleReservationId(Integer shuttleReservationId);

	// 刪除特定座位的所有預約
	void deleteBySeatId(Integer seatId);

	// 計算特定預約的座位數量
	@Query("SELECT COUNT(s) FROM ShuttleReservationSeat s WHERE s.shuttleReservationId = :shuttleReservationId")
	Long countSeatsByReservationId(@Param("shuttleReservationId") Integer shuttleReservationId);

	// 計算特定座位的預約數量
	@Query("SELECT COUNT(s) FROM ShuttleReservationSeat s WHERE s.seatId = :seatId")
	Long countReservationsBySeatId(@Param("seatId") Integer seatId);

	@Query("SELECT srs.seatId FROM ShuttleReservationSeat srs " + "JOIN srs.shuttleReservation sr "
			+ "WHERE sr.shuttleDate = :shuttleDate AND sr.shuttleSchedule.shuttleScheduleId = :scheduleId "
			+ "AND sr.shuttleReservationStatus = 1")
	List<Integer> findOccupiedSeatIds(@Param("shuttleDate") LocalDate shuttleDate,
			@Param("scheduleId") Integer scheduleId);

}
