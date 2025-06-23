package com.islevilla.lai.shuttle.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.*;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "temp_shuttle_reservation_request")
@Data
public class TempShuttleRVRequest {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(name = "member_id", nullable = false)
    private Integer memberId;
    
    @Column(name = "room_reservation_id", nullable = false)
    private Integer roomReservationId;
    
    @Column(name = "shuttle_date", nullable = false)
    private LocalDate shuttleDate;
    
    @Column(name = "shuttle_number", nullable = false)
    private Integer shuttleNumber;
    
    @Column(name = "shuttle_direction", nullable = false)
    private Integer shuttleDirection;
    
    @Column(name = "selected_schedule_id")
    private Integer selectedScheduleId;
    
    @Column(name = "selected_seat_ids")
    private String selectedSeatIds; // 儲存為逗號分隔的字串
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
    
    // 輔助方法：將List<Integer>轉換為字串
    public void setSelectedSeatIdsList(List<Integer> seatIds) {
        if (seatIds != null && !seatIds.isEmpty()) {
            this.selectedSeatIds = seatIds.stream()
                .map(String::valueOf)
                .reduce((a, b) -> a + "," + b)
                .orElse("");
        }
    }
    
    // 輔助方法：將字串轉換為List<Integer>
    public List<Integer> getSelectedSeatIdsList() {
        if (selectedSeatIds != null && !selectedSeatIds.isEmpty()) {
            return List.of(selectedSeatIds.split(","))
                .stream()
                .map(Integer::parseInt)
                .toList();
        }
        return List.of();
    }
}
