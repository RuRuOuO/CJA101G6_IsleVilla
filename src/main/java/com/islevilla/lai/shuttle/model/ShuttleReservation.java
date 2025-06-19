package com.islevilla.lai.shuttle.model;

import java.time.LocalDate;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Entity
@Table(name = "shuttle_reservation")
@Data
public class ShuttleReservation {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "shuttle_reservation_id")
	private Integer shuttleReservationId;

	@Column(name = "member_id", nullable = false)
	@NotNull(message = "會員編號不能為空")
	private Integer memberId;

	@Column(name = "room_reservation_id", nullable = false)
	@NotNull(message = "訂房編號不能為空")
	private Integer roomReservationId;

	@Column(name = "shuttle_date", nullable = false)
	@NotNull(message = "請選擇接駁日期")
	private LocalDate shuttleDate;

	@Column(name = "shuttle_schedule_id", nullable = false)
	@NotNull(message = "請選擇接駁班次")
	private Integer shuttleScheduleId;

	@Column(name = "shuttle_direction", nullable = false)
	@NotNull(message = "請選擇去回程")
	private Integer shuttleDirection; // 0:去程 1:回程

	@Column(name = "shuttle_number", nullable = false)
	@NotNull(message = "請輸入接駁人數")
	@Min(value = 1, message = "接駁人數不能少於1人")
	private Integer shuttleNumber;

	@Column(name = "shuttle_reservation_status", nullable = false)
	@NotNull(message = "請選擇預約狀態")
	private Integer shuttleReservationStatus; // 0:取消 1:正常
}