package com.islevilla.lai.shuttle.model;

import java.time.LocalDate;
import java.util.List;

import com.islevilla.lai.members.model.Members;
import com.islevilla.wei.room.model.RoomRVOrder;

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

	// 多對一
	@ManyToOne
	@JoinColumn(name = "member_id", nullable = false)
	@NotNull(message = "會員編號不能為空")
	private Members members;

	// 多對一
	@ManyToOne
	@JoinColumn(name = "room_reservation_id", nullable = false)
	@NotNull(message = "訂房編號不能為空")
	private RoomRVOrder roomRVOrder;

	@Column(name = "shuttle_date", nullable = false)
	@NotNull(message = "請選擇接駁日期")
	private LocalDate shuttleDate;

	// 多對一
	@ManyToOne
	@JoinColumn(name = "shuttle_schedule_id", nullable = false)
	@NotNull(message = "請選擇接駁班次")
	private ShuttleSchedule shuttleSchedule;

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

	// 一對多
	@OneToMany(mappedBy = "shuttleReservation", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private List<ShuttleReservationSeat> shuttleReservationSeats;
}