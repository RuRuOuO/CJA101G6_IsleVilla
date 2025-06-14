package com.islevilla.chen.room.model;

import org.springframework.stereotype.Component;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name="room")
@Component
@Data
public class roomVO {
	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name= "room_id")
	@OneToMany(cascade=CascadeType.ALL, fetch=FetchType.EAGER, mappedBy="deptVO")
	private int roomId;
	
	@Column(name= "room_type")//FK
	private int roomTypeId;
	
	@Column(name= "room_status") //0:空房 1:入住中 2:待維修 3:待清潔 
	private byte roomStatus;


}
