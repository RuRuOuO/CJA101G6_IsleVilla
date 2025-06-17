package com.islevilla.chen.roomTypePhoto.model;

import org.springframework.stereotype.Component;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Entity
@Table(name = "room_type_photo")
@Data
@Component
public class RoomTypePhotoVO {
	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name = "room_type_photo_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@NotBlank(message = "房型圖片編號: 請勿空白")
	@Pattern(regexp="^[(0-9)]$", message="房型圖片編號:格式錯誤，請輸入數字")
	private int roomTypePhotoId;
	
	@Column(name = "room_type_id")
//	@ManyToOne //FK
//	@JoinColumn(name="room_type_id",referencedColumnName = "room_type_id")
	@NotBlank(message = "房型編號: 請勿空白")
	@Pattern(regexp="^[(0-9)]$", message="房型編號:格式錯誤，請輸入數字")
	private int roomTypeId;
	
	@Column(name = "room_type_photo")
	private byte[] roomTypePhoto;


}
