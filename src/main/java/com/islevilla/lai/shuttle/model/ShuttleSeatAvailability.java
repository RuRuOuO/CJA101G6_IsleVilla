package com.islevilla.lai.shuttle.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Entity
@Table(name = "shuttle_seat_availability")
@IdClass(ShuttleSeatAvailability.ShuttleSeatAvailabilityId.class)
@Data
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

	// IdClass 需要是一個獨立的類別
	public static class ShuttleSeatAvailabilityId implements Serializable {
		private static final long serialVersionUID = 1L;

		private Integer shuttleScheduleId;
		private LocalDate shuttleDate;

		// 預設建構子
		public ShuttleSeatAvailabilityId() {
		}

		// 帶參數建構子
		public ShuttleSeatAvailabilityId(Integer shuttleScheduleId, LocalDate shuttleDate) {
			this.shuttleScheduleId = shuttleScheduleId;
			this.shuttleDate = shuttleDate;
		}

		// Getter 和 Setter
		public Integer getShuttleScheduleId() {
			return shuttleScheduleId;
		}

		public void setShuttleScheduleId(Integer shuttleScheduleId) {
			this.shuttleScheduleId = shuttleScheduleId;
		}

		public LocalDate getShuttleDate() {
			return shuttleDate;
		}

		public void setShuttleDate(LocalDate shuttleDate) {
			this.shuttleDate = shuttleDate;
		}

		// 必須實作 equals 和 hashCode
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null || getClass() != obj.getClass())
				return false;
			ShuttleSeatAvailabilityId that = (ShuttleSeatAvailabilityId) obj;
			return Objects.equals(shuttleScheduleId, that.shuttleScheduleId)
					&& Objects.equals(shuttleDate, that.shuttleDate);
		}

		@Override
		public int hashCode() {
			return Objects.hash(shuttleScheduleId, shuttleDate);
		}

		@Override
		public String toString() {
			return "ShuttleSeatAvailabilityId{" + "shuttleScheduleId=" + shuttleScheduleId + ", shuttleDate="
					+ shuttleDate + '}';
		}
	}
}