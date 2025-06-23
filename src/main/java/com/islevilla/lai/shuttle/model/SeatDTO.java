package com.islevilla.lai.shuttle.model;

import lombok.Data;

@Data
public class SeatDTO {
	private Integer seatId;
    private String seatNumber;
    private Integer seatStatus; // 0:故障 1:正常
    private boolean occupied; // 是否已被預約
    
    public SeatDTO() {}
    
    public SeatDTO(Integer seatId, String seatNumber, Integer seatStatus, boolean occupied) {
        this.seatId = seatId;
        this.seatNumber = seatNumber;
        this.seatStatus = seatStatus;
        this.occupied = occupied;
    }
}
