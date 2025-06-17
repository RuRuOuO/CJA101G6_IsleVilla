package com.islevilla.ching.set.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "seat")
public class Seat {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "seat_id")
	private Integer seatId;
	
	@Column(name = "seat_number")
	private String seatNumber;
	
	@Column(name = "seat_status")
	private Integer seatStatus;
	
	public Integer getSeatId() {
		return seatId;
	}
	
	public void setSeatId(Integer seatId) {
		this.seatId = seatId;
	}

	public String getSeatNumber() {
		return seatNumber;
	}

	public void setSeatNumber(String seatNumber) {
		this.seatNumber = seatNumber;
	}

	public Integer getSeatStatus() {
		return seatStatus;
	}

	public void setSeatStatus(Integer seatStatus) {
		this.seatStatus = seatStatus;
	}

}
