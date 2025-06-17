package com.islevilla.ching.shuttleSchedule.model;

import java.time.LocalTime;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "shuttle_schedule")
public class ShuttleSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "shuttle_schedule_id")
    private Integer id;

    @Column(name = "shuttle_direction")
    private Integer direction;

    @Column(name = "shuttle_departure_time")
    private LocalTime departureTime;

    @Column(name = "shuttle_arrival_time")
    private LocalTime arrivalTime;


	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getDirection() {
		return direction;
	}

	public void setDirection(Integer direction) {
		this.direction = direction;
	}

	public LocalTime getDepartureTime() {
		return departureTime;
	}

	public void setDepartureTime(LocalTime departureTime) {
		this.departureTime = departureTime;
	}

	public LocalTime getArrivalTime() {
		return arrivalTime;
	}

	public void setArrivalTime(LocalTime arrivalTime) {
		this.arrivalTime = arrivalTime;
	}
}
