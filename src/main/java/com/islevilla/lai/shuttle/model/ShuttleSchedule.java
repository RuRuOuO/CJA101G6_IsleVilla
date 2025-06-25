package com.islevilla.lai.shuttle.model;

import java.time.LocalTime;
import java.util.List;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Entity
@Table(name = "shuttle_schedule")
@Data
public class ShuttleSchedule {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "shuttle_schedule_id")
	private Integer shuttleScheduleId;

	@Column(name = "shuttle_direction", nullable = false)
	@NotNull(message = "請選擇去回程")
	private Integer shuttleDirection; // 0:去程 1:回程

	@Column(name = "shuttle_departure_time", nullable = false)
	@NotNull(message = "請輸入出發時間")
	private LocalTime shuttleDepartureTime;

	@Column(name = "shuttle_arrival_time", nullable = false)
	@NotNull(message = "請輸入抵達時間")
	private LocalTime shuttleArrivalTime;
	
	@OneToMany(mappedBy = "shuttleSchedule")
    private List<ShuttleReservation> shuttleReservation;
	
	@OneToMany(mappedBy = "shuttleSchedule")
	private List<ShuttleSeatAvailability> shuttleSeatAvailability;
	
}
