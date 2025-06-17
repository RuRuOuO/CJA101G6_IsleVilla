package com.islevilla.chen.room.model;

import org.springframework.stereotype.Component;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;

@Entity
@Table(name="room")
@Component
@Data
public class RoomVO {
	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name= "room_id")
//	@OneToMany(cascade=CascadeType.ALL, fetch=FetchType.LAZY, mappedBy="room_id")
	@NotBlank(message = "房間號碼: 請勿空白")
	@Pattern(regexp="^[(0-9)]$", message="房間號碼:格式錯誤，請輸入數字")
	private int roomId;
	
	@Column(name= "room_type_id")
//	@ManyToOne//FK
//	@JoinColumn(name="room_type_id",referencedColumnName = "room_type_id")
	@NotBlank(message = "房型號碼: 請勿空白")
	@Pattern(regexp="^[(0-9)]$", message="房型編號:格式錯誤，請輸入數字")
	private int roomTypeId;
	
	@Column(name= "room_status") //0:空房 1:入住中 2:待維修 3:待清潔 
	//下拉選單
	private byte roomStatus;


}
