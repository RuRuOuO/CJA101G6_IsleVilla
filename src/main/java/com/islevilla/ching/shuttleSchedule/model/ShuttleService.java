package com.islevilla.ching.shuttleSchedule.model;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ShuttleService {

	@Autowired
	private ShuttleScheduleRepository shuttleScheduleRepository;

	public void addShuttle(ShuttleSchedule shuttleSchedule) {
		shuttleScheduleRepository.save(shuttleSchedule);
	}

	public void updateShuttle(ShuttleSchedule shuttleSchedule) {
		shuttleScheduleRepository.save(shuttleSchedule);
	}

	public void deleteShuttle(Integer shuttleId) {
		if (shuttleScheduleRepository.existsById(shuttleId)) {
			shuttleScheduleRepository.deleteById(shuttleId);
		}
	}

	public ShuttleSchedule getShuttleById(Integer shuttleId) {
		Optional<ShuttleSchedule> optional = shuttleScheduleRepository.findById(shuttleId);
		return optional.orElse(null);
		// public T orElse(T other) : 如果值存在就回傳其值，否則回傳other的值
	}

	public List<ShuttleSchedule> getAllShuttle() {
		return shuttleScheduleRepository.findAll();
	}
}
