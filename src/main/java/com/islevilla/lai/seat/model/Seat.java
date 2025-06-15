package com.islevilla.lai.seat.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Entity
@Table(name = "seat")
@Data
public class Seat {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "seat_id")
	private Integer seatId;
	
	@Column(name = "seat_number")
	@NotEmpty(message = "1")
	private String seatNumber;
	
	@Column(name = "seatStatus")
	@NotEmpty(message = "2")
	private Integer seatStatus;
}
