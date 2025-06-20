package com.islevilla.lai.shuttle.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "shuttle_seat_availability")
@Data
@IdClass(ShuttleSeatAvailability.ShuttleSeatAvailabilityId.class)
public class ShuttleSeatAvailability {
	@Id
	@Column(name = "shuttle_schedule_id")
	@NotNull(message = "接駁班次編號不能為空")
	private Integer shuttleScheduleId;

	@Id
	@Column(name = "shuttle_date")
	@NotNull(message = "請選擇日期")
	private LocalDate shuttleDate;

	@Column(name = "seat_quantity", nullable = false)
	@NotNull(message = "座位數量不能為空")
	@Min(value = 0, message = "座位數量不能小於0")
	private Integer seatQuantity = 100;

	@Column(name = "seat_updated_at", nullable = false)
	private LocalDateTime seatUpdatedAt;

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

	@Data
	@EqualsAndHashCode
	public static class ShuttleSeatAvailabilityId implements Serializable {
		private static final long serialVersionUID = 1L;
		
		private Integer shuttleScheduleId;
		private LocalDate shuttleDate;
	}
}