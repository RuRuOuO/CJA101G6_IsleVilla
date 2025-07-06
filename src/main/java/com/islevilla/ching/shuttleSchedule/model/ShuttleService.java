package com.islevilla.ching.shuttleSchedule.model;


import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.islevilla.ching.shuttleSeatAvailability.model.SeatAvailability;

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

	public void deleteShuttle(Integer shuttleScheduleId) {
		if (shuttleScheduleRepository.existsById(shuttleScheduleId)) {
			shuttleScheduleRepository.deleteById(shuttleScheduleId);
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
			Integer shuttleDirection, 
			java.time.LocalTime shuttleDepartureTime, 
			java.time.LocalTime shuttleArrivalTime) {
		return shuttleScheduleRepository.existsByShuttleDirectionAndShuttleDepartureTimeAndShuttleArrivalTime(shuttleDirection, shuttleDepartureTime, shuttleArrivalTime);
	}

	// 避免出發與抵達時間重複
	public boolean existsScheduleExcludingSelf(
			Integer shuttleDirection, 
			LocalTime shuttleDepartureTime, 
			LocalTime shuttleArrivalTime, 
			Integer selfshuttleScheduleId) {
	    return shuttleScheduleRepository.existsByShuttleDirectionAndShuttleDepartureTimeAndShuttleArrivalTimeAndShuttleScheduleIdNot(shuttleDirection, shuttleDepartureTime, shuttleArrivalTime, selfshuttleScheduleId);
	}
	
	public List<ShuttleSchedule> getShuttlesByIds(List<Integer> ids) {
	    return shuttleScheduleRepository.findAllById(ids);
	}

	// 查詢去程班次
	public List<ShuttleSchedule> getDepartureShuttles() {
	    return shuttleScheduleRepository.findByShuttleDirection(0);
	}

	// 查詢回程班次
	public List<ShuttleSchedule> getReturnShuttles() {
	    return shuttleScheduleRepository.findByShuttleDirection(1);
	}

	public List<ShuttleSchedule> getSchedulesUsedInSeatAvailability(List<SeatAvailability> allSeats) {

		return null;
	}
}
