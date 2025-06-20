package com.islevilla.ching.shuttleSeatAvailability.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

public class SeatAvailabilityId implements Serializable {
	
	private Integer scheduleId;
    private LocalDate date;
    
    public SeatAvailabilityId() {}
    
    public SeatAvailabilityId(Integer scheduleId, LocalDate date) {
        this.scheduleId = scheduleId;
        this.date = date;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SeatAvailabilityId)) return false;
        SeatAvailabilityId that = (SeatAvailabilityId) o;
        return Objects.equals(scheduleId, that.scheduleId) && Objects.equals(date, that.date);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(scheduleId, date);
    }

    // Getter & Setter（如需）
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
}
