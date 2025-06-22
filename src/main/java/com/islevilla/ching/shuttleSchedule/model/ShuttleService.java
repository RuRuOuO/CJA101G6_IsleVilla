package com.islevilla.ching.shuttleSchedule.model;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class ShuttleService {

	@Autowired
	@Qualifier("chingShuttleScheduleRepository")
	private ShuttleScheduleRepository shuttleScheduleRepository;

	public void addShuttle(ShuttleSchedule shuttleSchedule) {
		shuttleScheduleRepository.save(shuttleSchedule);
	}

	public void updateShuttle(ShuttleSchedule shuttleSchedule) {
		shuttleScheduleRepository.save(shuttleSchedule);
	}

	public void deleteShuttle(Integer Id) {
		if (shuttleScheduleRepository.existsById(Id)) {
			shuttleScheduleRepository.deleteById(Id);
		}
	}

	public ShuttleSchedule getShuttleById(Integer id) {
		Optional<ShuttleSchedule> optional = shuttleScheduleRepository.findById(id);
		return optional.orElse(null);
		// public T orElse(T other) : 如果值存在就回傳其值，否則回傳other的值
	}

	public List<ShuttleSchedule> getAllShuttle() {
		return shuttleScheduleRepository.findAll();
	}

	// 避免抵達時間早於出發時間
	public boolean existsSchedule(
			Integer direction, 
			java.time.LocalTime departure, 
			java.time.LocalTime arrival) {
		return shuttleScheduleRepository.existsByDirectionAndDepartureTimeAndArrivalTime(direction, departure, arrival);
	}

	// 避免出發與抵達時間重複
	public boolean existsScheduleExcludingSelf(
			Integer direction, 
			LocalTime departure, 
			LocalTime arrival, 
			Integer selfId) {
	    return shuttleScheduleRepository.existsByDirectionAndDepartureTimeAndArrivalTimeAndIdNot(direction, departure, arrival, selfId);
	}

}
