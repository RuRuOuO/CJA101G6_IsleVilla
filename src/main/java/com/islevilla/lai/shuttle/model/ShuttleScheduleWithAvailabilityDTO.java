package com.islevilla.lai.shuttle.model;

import java.time.LocalTime;

import lombok.Data;

@Data
public class ShuttleScheduleWithAvailabilityDTO {
	private Integer shuttleScheduleId;
	private Integer shuttleDirection; // 0:去程 1:回程
	private LocalTime shuttleDepartureTime;
	private LocalTime shuttleArrivalTime;
	private Integer availableSeats; // 可用座位數

	public ShuttleScheduleWithAvailabilityDTO() {
	}

	public ShuttleScheduleWithAvailabilityDTO(Integer shuttleScheduleId, Integer shuttleDirection,
			LocalTime shuttleDepartureTime, LocalTime shuttleArrivalTime, Integer availableSeats) {
		this.shuttleScheduleId = shuttleScheduleId;
		this.shuttleDirection = shuttleDirection;
		this.shuttleDepartureTime = shuttleDepartureTime;
		this.shuttleArrivalTime = shuttleArrivalTime;
		this.availableSeats = availableSeats;
	}
}
