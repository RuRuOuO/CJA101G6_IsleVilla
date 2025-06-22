package com.islevilla.lai.shuttle.model;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface SeatRepository extends JpaRepository<Seat, Integer> {

	// 查詢正常狀態的座位
	List<Seat> findBySeatStatus(Integer seatStatus);

	// 根據座位號碼查詢
	Seat findBySeatNumber(String seatNumber);

	// 查詢可用座位數量
	@Query("SELECT COUNT(s) FROM Seat s WHERE s.seatStatus = 1")
	long countAvailableSeats();

}
