package com.islevilla.lai.shuttle.model;

import java.time.LocalDate;
import java.util.List;

import lombok.Data;

@Data
public class TempShuttleRVSummaryDTO {
	private Integer memberId;
    private Integer roomReservationId;
    private LocalDate shuttleDate;
    private Integer shuttleDirection;
    private Integer shuttleNumber;
    private ShuttleScheduleWithAvailabilityDTO schedule;
    private List<SeatDTO> selectedSeats;
}
