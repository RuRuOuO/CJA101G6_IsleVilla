//package com.islevilla.lai.shuttlereservation.model;
//
//import java.sql.Date;
//
//import jakarta.persistence.Column;
//import jakarta.persistence.Entity;
//import jakarta.persistence.GeneratedValue;
//import jakarta.persistence.GenerationType;
//import jakarta.persistence.Id;
//import jakarta.persistence.JoinColumn;
//import jakarta.persistence.ManyToOne;
//import jakarta.persistence.Table;
//import jakarta.validation.constraints.NotEmpty;
//import lombok.Data;
//
//@Entity
//@Table(name = "shuttle_reservation")
//@Data
//public class ShuttleReservation {
//	@Column(name = "shuttle_reservation_id")
//	@GeneratedValue(strategy = GenerationType.IDENTITY)
//	@Id
//	private Integer shuttleReservationId;
//	
//	@ManyToOne
//	@JoinColumn(name = "member_id", referencedColumnName = "member_id", nullable = false)
//	private Integer memberId;
//
//	@ManyToOne
//	@JoinColumn(name = "room_reservation_id", referencedColumnName = "room_reservation_id", nullable = false)
//	private Integer roomReservationId;
//	
//	@Column(name = "shuttle_date")
//	@NotEmpty(message = "請輸入接駁日期")
//	private Date shuttleDate;
//
//	@Column(name = "shuttle_schedule_id")
//	@NotEmpty(message = "請輸入接駁班次編號")
//	private Integer shuttleScheduleId;
//
//	@Column(name = "shuttle_direction")
//	@NotEmpty(message = "請選擇去/回程")
//	private Integer shuttleDirection;
//
//	@Column(name = "shuttle_number")
//	@NotEmpty(message = "請輸入接駁人數")
//	private Integer shuttleNumber;
//
//	@Column(name = "shuttle_reservation_status")
//	@NotEmpty(message = "請輸入接駁預約狀態")
//	private Integer shuttleReservationStatus;
//}
