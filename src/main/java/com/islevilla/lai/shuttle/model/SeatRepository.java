package com.islevilla.lai.shuttle.model;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface SeatRepository extends JpaRepository<Seat, Integer> {

	// 查詢不可用座位數量
	@Query("SELECT COUNT(s) FROM Seat s WHERE s.seatStatus = 0")
	Integer countUnavailableSeats();

	// 查詢所有座位並按座位號排序
	List<Seat> findAllByOrderBySeatNumber();
}
