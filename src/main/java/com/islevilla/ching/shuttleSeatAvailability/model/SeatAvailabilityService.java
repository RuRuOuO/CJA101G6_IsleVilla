package com.islevilla.ching.shuttleSeatAvailability.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.islevilla.ching.shuttleSchedule.model.ShuttleService;

@Service
public class SeatAvailabilityService {

	@Autowired
	private SeatAvailabilityRepository seatAvailabilityRepository;

	@Autowired
	private ShuttleService shuttleService;

	// 新增或修改座位資料
	public void addSeatAvailability(SeatAvailability seatAvailability) {
		seatAvailability.setSeatUpdateAt(LocalDateTime.now()); // ⏰ 寫入當下時間
		seatAvailabilityRepository.save(seatAvailability);
	}

	// 取得所有座位資料
	public List<SeatAvailability> getAllSeatAvailability() {
		return seatAvailabilityRepository.findAllByOrderByDateAsc();
	}

	// 用複合主鍵刪除
	public void deleteSeatAvailability(SeatAvailabilityId id) {
		if (seatAvailabilityRepository.existsById(id)) {
			seatAvailabilityRepository.deleteById(id);
		}
	}

	// 用複合主鍵查詢
	public SeatAvailability getSeatById(SeatAvailabilityId scheduleId) {
		Optional<SeatAvailability> optional = seatAvailabilityRepository.findById(scheduleId);
		return optional.orElse(null);
	}

	// 修改
	public void updateSeatAvailability(SeatAvailability seatAvailability) {
		seatAvailability.setSeatUpdateAt(LocalDateTime.now()); // ⏰ 寫入當下時間
		seatAvailabilityRepository.save(seatAvailability);
	}

	public List<LocalDate> getAllAvailableDates() {
		return seatAvailabilityRepository.findAll()
				.stream()
				.map(SeatAvailability::getDate)
				.distinct()
				.sorted()
				.toList();
	}

}
