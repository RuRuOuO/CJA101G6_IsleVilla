package com.islevilla.lai.shuttle.model;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ShuttleSeatAvailabilityService {

	@Autowired
	private ShuttleSeatAvailabilityRepository shuttleSeatAvailabilityRepository;

	public List<ShuttleSeatAvailability> getAllSeatAvailability() {
		return shuttleSeatAvailabilityRepository.findAll();
	}

	public Optional<ShuttleSeatAvailability> getSeatAvailabilityById(
			ShuttleSeatAvailability.ShuttleSeatAvailabilityId id) {
		return shuttleSeatAvailabilityRepository.findById(id);
	}

//	public List<ShuttleSeatAvailability> getSeatAvailabilityByScheduleId(Integer scheduleId) {
//		return shuttleSeatAvailabilityRepository.findByShuttleScheduleId(scheduleId);
//	}

//	public List<ShuttleSeatAvailability> getSeatAvailabilityByDate(LocalDate date) {
//		return shuttleSeatAvailabilityRepository.findByShuttleDate(date);
//	}

//	public Optional<ShuttleSeatAvailability> getSeatAvailabilityByScheduleAndDate(Integer scheduleId, LocalDate date) {
//		return shuttleSeatAvailabilityRepository.findByShuttleScheduleIdAndShuttleDate(scheduleId, date);
//	}

	@Transactional
	public ShuttleSeatAvailability saveSeatAvailability(ShuttleSeatAvailability seatAvailability) {
		return shuttleSeatAvailabilityRepository.save(seatAvailability);
	}

//	@Transactional
//	public ShuttleSeatAvailability updateSeatQuantity(Integer scheduleId, LocalDate date, Integer quantity) {
//		Optional<ShuttleSeatAvailability> availabilityOpt = shuttleSeatAvailabilityRepository
//				.findByShuttleScheduleIdAndShuttleDate(scheduleId, date);
//
//		if (availabilityOpt.isPresent()) {
//			ShuttleSeatAvailability availability = availabilityOpt.get();
//			availability.setSeatQuantity(quantity);
//			return shuttleSeatAvailabilityRepository.save(availability);
//		} else {
//			// 如果不存在，創建新記錄
//			ShuttleSeatAvailability newAvailability = new ShuttleSeatAvailability();
//			newAvailability.setShuttleScheduleId(scheduleId);
//			newAvailability.setShuttleDate(date);
//			newAvailability.setSeatQuantity(quantity);
//			return shuttleSeatAvailabilityRepository.save(newAvailability);
//		}
//	}

	@Transactional
//	public boolean reserveSeats(Integer scheduleId, LocalDate date, Integer seatCount) {
//		Optional<ShuttleSeatAvailability> availabilityOpt = shuttleSeatAvailabilityRepository
//				.findByShuttleScheduleIdAndShuttleDate(scheduleId, date);
//
//		if (availabilityOpt.isPresent()) {
//			ShuttleSeatAvailability availability = availabilityOpt.get();
//			if (availability.getSeatQuantity() >= seatCount) {
//				availability.setSeatQuantity(availability.getSeatQuantity() - seatCount);
//				shuttleSeatAvailabilityRepository.save(availability);
//				return true;
//			}
//		}
//		return false;
//	}

	public void deleteSeatAvailability(ShuttleSeatAvailability.ShuttleSeatAvailabilityId id) {
		shuttleSeatAvailabilityRepository.deleteById(id);
	}

}
