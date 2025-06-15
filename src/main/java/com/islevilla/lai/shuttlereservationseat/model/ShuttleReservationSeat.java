//package com.islevilla.lai.shuttlereservationseat.model;
//
//import jakarta.persistence.Entity;
//import jakarta.persistence.JoinColumn;
//import jakarta.persistence.ManyToOne;
//import jakarta.persistence.Table;
//import lombok.Data;
//
//@Entity
//@Table(name = "shuttle_reservation_seat")
//@Data
//public class ShuttleReservationSeat {
//	
//	@ManyToOne
//	@JoinColumn(name = "shuttle_reservation_id", referencedColumnName = "shuttle_reservation_id", nullable = false)
//	private Integer shuttleReservationId;
//	
//	@ManyToOne
//	@JoinColumn(name = "seat_id", referencedColumnName = "seat_id", nullable = false)
//	private Integer seatId;
//}
