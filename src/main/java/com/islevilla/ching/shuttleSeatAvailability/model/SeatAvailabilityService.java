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

	// 新增資料
	public void addSeatAvailability(SeatAvailability seatAvailability) {
		seatAvailability.setSeatUpdatedAt(LocalDateTime.now()); // 寫入當下時間
		seatAvailabilityRepository.save(seatAvailability);
	}

	// 取得所有座位資料
	public List<SeatAvailability> getAllSeatAvailability() {
		return seatAvailabilityRepository.findAllByOrderByShuttleDateAsc();
	}

	// 用複合主鍵刪除
	public void deleteSeatAvailability(Integer scheduleId, LocalDate date) {
	    var id = new SeatAvailability.ShuttleSeatAvailabilityId(scheduleId, date);
	    if (seatAvailabilityRepository.existsById(id)) {
	        seatAvailabilityRepository.deleteById(id);
	    }
	}

	// 用複合主鍵查詢
	public SeatAvailability getSeatById(Integer scheduleId, LocalDate shuttleDate) {
	    var id = new SeatAvailability.ShuttleSeatAvailabilityId(scheduleId, shuttleDate);
	    return seatAvailabilityRepository.findById(id).orElse(null);
	}

	// 修改
	public void updateSeatAvailability(SeatAvailability seatAvailability) {
		seatAvailability.setSeatUpdatedAt(LocalDateTime.now()); // ⏰ 寫入當下時間
		seatAvailabilityRepository.save(seatAvailability);
	}
	
	//查詢所有日期
	public List<LocalDate> getAllAvailableDates() {
		return seatAvailabilityRepository.findAll()
				.stream()
				.map(SeatAvailability::getShuttleDate)
				.distinct()
				.sorted()
				.toList();
	}

}
