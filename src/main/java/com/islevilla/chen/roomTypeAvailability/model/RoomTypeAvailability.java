package com.islevilla.chen.roomTypeAvailability.model;

import java.util.Date;

import org.springframework.stereotype.Component;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name="room_type_availability")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Component
public class RoomTypeAvailability {
	private static final long serialVersionUID = 1L;
	
	@Id
	@OrderBy("room_type_id asc")
	@Column(name = "room_type_id")
//	@ManyToOne
//	@JoinColumn(name="room_type_id",referencedColumnName = "room_type_id")//FK、複合
	@NotBlank(message = "房型編號: 請勿空白")
	@Pattern(regexp="^[(0-9)]$", message="房型編號:格式錯誤，請輸入數字")
	private int roomTypeId;
	
//	@Id
	@Column(name = "room_type_availability_date") //複合
	@NotBlank(message = "日期: 請勿空白")
	private Date roomTypeAvailabilityDate;
	
	@Column(name="room_type_availability_count")
	private int roomTypeAvailabilityCount;

}
