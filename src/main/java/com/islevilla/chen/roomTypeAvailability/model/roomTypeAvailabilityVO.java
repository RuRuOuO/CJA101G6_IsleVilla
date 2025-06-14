package com.islevilla.chen.roomTypeAvailability.model;

import java.util.Date;

import org.springframework.stereotype.Component;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name="room_type_availability")
@Data
@Component
public class roomTypeAvailabilityVO {
	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name= "room_type_id") //FK、複合
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int roomTypeId;
	
	@Column(name = "room_type_availability_date") //複合
	private Date roomTypeAvailabilityDate;
	
	@Column(name="room_type_availability_count")
	private int roomTypeAvailabilityCount;

}
