package com.islevilla.lai.shuttle.model;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ShuttleReservationSeatService {

	@Autowired
	private ShuttleReservationSeatRepository shuttleReservationSeatRepository;

	// 儲存座位預約關聯
	public ShuttleReservationSeat save(ShuttleReservationSeat shuttleReservationSeat) {
		return shuttleReservationSeatRepository.save(shuttleReservationSeat);
	}

	// 根據預約查詢所有座位
	public List<ShuttleReservationSeat> findByShuttleReservation(ShuttleReservation shuttleReservation) {
		return shuttleReservationSeatRepository.findByShuttleReservation(shuttleReservation);
	}

	// 檢查座位預約關聯是否存在
	public boolean existsByShuttleReservationIdAndSeatId(Integer shuttleReservationId, Integer seatId) {
		return shuttleReservationSeatRepository.existsByShuttleReservationIdAndSeatId(shuttleReservationId, seatId);
	}

	// 刪除特定預約的所有座位
	public void deleteByShuttleReservationId(Integer shuttleReservationId) {
		shuttleReservationSeatRepository.deleteByShuttleReservationId(shuttleReservationId);
	}

}
