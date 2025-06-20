package com.islevilla.lai.shuttle.model;

import java.io.Serializable;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "shuttle_reservation_seat")
@Data
@IdClass(ShuttleReservationSeat.ShuttleReservationSeatId.class)
public class ShuttleReservationSeat {
	@Id
	@Column(name = "shuttle_reservation_id")
	private Integer shuttleReservationId;

	@Id
	@Column(name = "seat_id")
	private Integer seatId;

	@Data
	@EqualsAndHashCode
	public static class ShuttleReservationSeatId implements Serializable {
		private static final long serialVersionUID = 1L;

		private Integer shuttleReservationId;
		private Integer seatId;
	}
}