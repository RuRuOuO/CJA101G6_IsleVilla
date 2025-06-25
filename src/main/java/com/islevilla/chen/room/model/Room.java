package com.islevilla.chen.room.model;

import org.springframework.stereotype.Component;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name="room")
@Component
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Room implements java.io.Serializable {
	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name= "room_id")
//	@OneToMany(cascade=CascadeType.ALL, fetch=FetchType.LAZY, mappedBy="room_id")
	@NotNull(message = "房間號碼: 請勿空白")
	@Min(value=1, message="房型編號:格式錯誤，請輸入數字")
	private Integer roomId;
	
	@Column(name= "room_type_id")
//	@ManyToOne//FK
//	@JoinColumn(name="room_type_id",referencedColumnName = "room_type_id")
	@NotNull(message = "房型號碼: 請勿空白")
	@Min(value=1, message="房型編號:格式錯誤，請輸入數字")
	private Integer roomTypeId;
	
	@Column(name= "room_status") //0:空房 1:入住中 2:待維修 3:待清潔 4：停用
	//下拉選單
	private Byte roomStatus;


}
