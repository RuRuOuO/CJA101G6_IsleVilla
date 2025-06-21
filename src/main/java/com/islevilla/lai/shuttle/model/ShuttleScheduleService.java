package com.islevilla.lai.shuttle.model;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ShuttleScheduleService {

	@Autowired
	private ShuttleScheduleRepository shuttleScheduleRepository;

	public List<ShuttleSchedule> getAllSchedules() {
		return shuttleScheduleRepository.findAll();
	}

	public Optional<ShuttleSchedule> getScheduleById(Integer id) {
		return shuttleScheduleRepository.findById(id);
	}

	public List<ShuttleSchedule> getSchedulesByDirection(Integer direction) {
		return shuttleScheduleRepository.findByShuttleDirection(direction);
	}

	public List<ShuttleSchedule> getOutboundSchedules() {
		return shuttleScheduleRepository.findByShuttleDirectionOrderByShuttleDepartureTime(0);
	}

	public List<ShuttleSchedule> getInboundSchedules() {
		return shuttleScheduleRepository.findByShuttleDirectionOrderByShuttleDepartureTime(1);
	}

	public ShuttleSchedule saveSchedule(ShuttleSchedule schedule) {
		return shuttleScheduleRepository.save(schedule);
	}

	public void deleteSchedule(Integer id) {
		shuttleScheduleRepository.deleteById(id);
	}

	public boolean existsById(Integer id) {
		return shuttleScheduleRepository.existsById(id);
	}

}
