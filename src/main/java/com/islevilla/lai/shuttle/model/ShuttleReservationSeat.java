package com.islevilla.lai.shuttle.model;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "shuttle_reservation_seat")
@IdClass(ShuttleReservationSeat.ShuttleReservationSeatId.class)
@Data
public class ShuttleReservationSeat {
	
	@Id
	@Column(name = "shuttle_reservation_id")
	private Integer shuttleReservationId;
	
	@Id
	@Column(name = "seat_id")
	private Integer seatId;
	
	@ManyToOne
    @JoinColumn(name = "shuttle_reservation_id", insertable = false, updatable = false)
    private ShuttleReservation shuttleReservation;
	
	@ManyToOne
    @JoinColumn(name = "seat_id", insertable = false, updatable = false)
    private Seat seat;
	
	// IdClass 需要是獨立的類別，不能是內部類別
	public static class ShuttleReservationSeatId implements Serializable {
        private static final long serialVersionUID = 1L;

        private Integer shuttleReservationId;
        private Integer seatId;
        
        // 預設建構子
        public ShuttleReservationSeatId() {}
        
        // 帶參數建構子
        public ShuttleReservationSeatId(Integer shuttleReservationId, Integer seatId) {
            this.shuttleReservationId = shuttleReservationId;
            this.seatId = seatId;
        }
        
        // Getter 和 Setter
        public Integer getShuttleReservationId() {
            return shuttleReservationId;
        }
        
        public void setShuttleReservationId(Integer shuttleReservationId) {
            this.shuttleReservationId = shuttleReservationId;
        }
        
        public Integer getSeatId() {
            return seatId;
        }
        
        public void setSeatId(Integer seatId) {
            this.seatId = seatId;
        }
        
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