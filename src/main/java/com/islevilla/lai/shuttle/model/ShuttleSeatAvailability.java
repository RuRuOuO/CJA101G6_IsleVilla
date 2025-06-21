package com.islevilla.lai.shuttle.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "shuttle_seat_availability")
@Data
public class ShuttleSeatAvailability {
	
	@EmbeddedId
    private ShuttleSeatAvailabilityId id;

    @Column(name = "seat_quantity", nullable = false)
    @NotNull(message = "座位數量不能為空")
    @Min(value = 0, message = "座位數量不能小於0")
    private Integer seatQuantity = 100;

    @Column(name = "seat_updated_at", nullable = false)
    private LocalDateTime seatUpdatedAt;
    
    // 加上與 ShuttleSchedule 的關聯
    @ManyToOne
    @JoinColumn(name = "shuttle_schedule_id", insertable = false, updatable = false)
    private ShuttleSchedule shuttleSchedule;

    @PrePersist
    protected void onCreate() {
        seatUpdatedAt = LocalDateTime.now();
        if (seatQuantity == null) {
            seatQuantity = 100;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        seatUpdatedAt = LocalDateTime.now();
    }

    @Embeddable
    @Data
    public static class ShuttleSeatAvailabilityId implements Serializable {
        private static final long serialVersionUID = 1L;
        
        @Column(name = "shuttle_schedule_id")
        @NotNull(message = "接駁班次編號不能為空")
        private Integer shuttleScheduleId;
        
        @Column(name = "shuttle_date")
        @NotNull(message = "請選擇日期")
        private LocalDate shuttleDate;
        
        // 必須實作 equals 和 hashCode
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ShuttleSeatAvailabilityId that = (ShuttleSeatAvailabilityId) o;
            return Objects.equals(shuttleScheduleId, that.shuttleScheduleId) &&
                   Objects.equals(shuttleDate, that.shuttleDate);
        }
        
        @Override
        public int hashCode() {
            return Objects.hash(shuttleScheduleId, shuttleDate);
        }
    }
}