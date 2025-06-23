package com.islevilla.ching.shuttleSeatAvailability.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.islevilla.ching.shuttleSchedule.model.ShuttleSchedule;

import jakarta.persistence.*;


@Entity
@Table(name ="shuttle_seat_availability" )
@IdClass(SeatAvailabilityId.class)
public class SeatAvailability {
	
	@Id
	@Column(name = "shuttle_schedule_id")
	private Integer scheduleId;
	
	@Id
	@Column(name = "shuttle_date")
	private LocalDate date;
	
	@Column(name= "seat_quantity")
	private Integer seatQuantity;
	
	@Column(name= "seat_updated_at")
	private LocalDateTime seatUpdateAt;
	
	@ManyToOne
	@JoinColumn(name = "shuttle_schedule_id", referencedColumnName = "shuttle_schedule_id", insertable = false, updatable = false)
	private ShuttleSchedule schedule;

	public Integer getScheduleId() {
		return scheduleId;
	}

	public void setScheduleId(Integer scheduleId) {
		this.scheduleId = scheduleId;
	}

	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}

	public Integer getSeatQuantity() {
		return seatQuantity;
	}

	public void setSeatQuantity(Integer seatQuantity) {
		this.seatQuantity = seatQuantity;
	}

	public LocalDateTime getSeatUpdateAt() {
		return seatUpdateAt;
	}

	public void setSeatUpdateAt(LocalDateTime seatUpdateAt) {
		this.seatUpdateAt = seatUpdateAt;
	}

	public ShuttleSchedule getSchedule() {
		return schedule;
	}

	public void setSchedule(ShuttleSchedule schedule) {
		this.schedule = schedule;
	}

	
}
