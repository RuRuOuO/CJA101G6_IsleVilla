package com.islevilla.lai.shuttle.model;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "shuttle_reservation_seat")
@Data
public class ShuttleReservationSeat {
	@EmbeddedId
	private ShuttleReservationSeatId id;
	
	@ManyToOne
    @JoinColumn(name = "shuttle_reservation_id", insertable = false, updatable = false)
    private ShuttleReservation shuttleReservation;
	
	@ManyToOne
    @JoinColumn(name = "seat_id", insertable = false, updatable = false)
    private Seat seat;
	
	@Embeddable
    @Data
    public static class ShuttleReservationSeatId implements Serializable {
        private static final long serialVersionUID = 1L;

        @Column(name = "shuttle_reservation_id")
        private Integer shuttleReservationId;
        
        @Column(name = "seat_id")
        private Integer seatId;
        
        // 必須實作 equals 和 hashCode
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ShuttleReservationSeatId that = (ShuttleReservationSeatId) o;
            return Objects.equals(shuttleReservationId, that.shuttleReservationId) &&
                   Objects.equals(seatId, that.seatId);
        }
        
        @Override
        public int hashCode() {
            return Objects.hash(shuttleReservationId, seatId);
        }
    }
}