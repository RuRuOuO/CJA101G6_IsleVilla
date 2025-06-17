package com.islevilla.lai.shuttle.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Entity
@Table(name = "seat")
@Data
public class Seat {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "seat_id")
	private Integer seatId;

	@Column(name = "seat_number", nullable = false, length = 10)
	@NotBlank(message = "請輸入座號")
	@Size(max = 10, message = "座號長度不能超過10個字")
	private String seatNumber;

	@Column(name = "seat_status", nullable = false)
	@NotNull(message = "請選擇座位狀態")
	private Integer seatStatus; // 0:故障 1:正常
}
