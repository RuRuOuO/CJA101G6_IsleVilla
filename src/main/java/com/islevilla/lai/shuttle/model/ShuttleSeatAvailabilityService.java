package com.islevilla.lai.shuttle.model;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ShuttleSeatAvailabilityService {

	@Autowired
	private ShuttleSeatAvailabilityRepository shuttleSeatAvailabilityRepository;

	// 獲取特定班次和日期的座位設定
	public Optional<ShuttleSeatAvailability> getSeatAvailability(Integer scheduleId, LocalDate date) {
		return shuttleSeatAvailabilityRepository.findByShuttleScheduleIdAndShuttleDate(scheduleId, date);
	}

	// 取消預訂（增加可用座位數量）
	@Transactional
	public boolean cancelReservation(Integer scheduleId, LocalDate date, Integer seatCount) {
		Optional<ShuttleSeatAvailability> availabilityOpt = getSeatAvailability(scheduleId, date);

		if (availabilityOpt.isPresent()) {
			ShuttleSeatAvailability availability = availabilityOpt.get();
			availability.setSeatQuantity(availability.getSeatQuantity() + seatCount);
			shuttleSeatAvailabilityRepository.save(availability);
			return true;
		}
		return false;
	}

}
