package com.islevilla.lai.shuttle.model;

import java.util.List;
import java.util.Optional;

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

	// 批量儲存座位預約關聯
	public List<ShuttleReservationSeat> saveAll(List<ShuttleReservationSeat> shuttleReservationSeats) {
		return shuttleReservationSeatRepository.saveAll(shuttleReservationSeats);
	}

	// 根據複合主鍵查詢座位預約關聯
	public Optional<ShuttleReservationSeat> findById(Integer shuttleReservationId, Integer seatId) {
		ShuttleReservationSeat.ShuttleReservationSeatId id = new ShuttleReservationSeat.ShuttleReservationSeatId();
		id.setShuttleReservationId(shuttleReservationId);
		id.setSeatId(seatId);
		return shuttleReservationSeatRepository.findById(id);
	}

	// 查詢所有座位預約關聯
	public List<ShuttleReservationSeat> findAll() {
		return shuttleReservationSeatRepository.findAll();
	}

	// 根據預約ID查詢所有座位
	public List<ShuttleReservationSeat> findByShuttleReservationId(Integer shuttleReservationId) {
		return shuttleReservationSeatRepository.findByShuttleReservationId(shuttleReservationId);
	}

	// 根據座位ID查詢所有預約
	public List<ShuttleReservationSeat> findBySeatId(Integer seatId) {
		return shuttleReservationSeatRepository.findBySeatId(seatId);
	}

	// 檢查座位預約關聯是否存在
	public boolean existsByShuttleReservationIdAndSeatId(Integer shuttleReservationId, Integer seatId) {
		return shuttleReservationSeatRepository.existsByShuttleReservationIdAndSeatId(shuttleReservationId, seatId);
	}

	// 刪除座位預約關聯
	public void deleteById(Integer shuttleReservationId, Integer seatId) {
		ShuttleReservationSeat.ShuttleReservationSeatId id = new ShuttleReservationSeat.ShuttleReservationSeatId();
		id.setShuttleReservationId(shuttleReservationId);
		id.setSeatId(seatId);
		shuttleReservationSeatRepository.deleteById(id);
	}

	// 刪除特定預約的所有座位
	public void deleteByShuttleReservationId(Integer shuttleReservationId) {
		shuttleReservationSeatRepository.deleteByShuttleReservationId(shuttleReservationId);
	}

	// 刪除特定座位的所有預約
	public void deleteBySeatId(Integer seatId) {
		shuttleReservationSeatRepository.deleteBySeatId(seatId);
	}

	// 計算特定預約的座位數量
	public Long countSeatsByReservationId(Integer shuttleReservationId) {
		return shuttleReservationSeatRepository.countSeatsByReservationId(shuttleReservationId);
	}

	// 計算特定座位的預約數量
	public Long countReservationsBySeatId(Integer seatId) {
		return shuttleReservationSeatRepository.countReservationsBySeatId(seatId);
	}

	// 為特定預約新增座位
	public ShuttleReservationSeat addSeatToReservation(Integer shuttleReservationId, Integer seatId) {
		if (existsByShuttleReservationIdAndSeatId(shuttleReservationId, seatId)) {
			throw new IllegalArgumentException("座位已經被預約");
		}

		ShuttleReservationSeat shuttleReservationSeat = new ShuttleReservationSeat();
		shuttleReservationSeat.setShuttleReservationId(shuttleReservationId);
		shuttleReservationSeat.setSeatId(seatId);

		return save(shuttleReservationSeat);
	}

	// 為特定預約新增多個座位
	public List<ShuttleReservationSeat> addSeatsToReservation(Integer shuttleReservationId, List<Integer> seatIds) {
		return seatIds.stream().map(seatId -> addSeatToReservation(shuttleReservationId, seatId)).toList();
	}
	
}
