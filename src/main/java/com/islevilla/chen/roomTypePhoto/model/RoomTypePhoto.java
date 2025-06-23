package com.islevilla.chen.roomTypePhoto.model;

import org.springframework.stereotype.Component;

import com.islevilla.chen.roomType.model.RoomType;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "room_type_photo")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Component
public class RoomTypePhoto {
	
	@Id
	@Column(name = "room_type_photo_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@NotNull(message = "房型圖片編號: 請勿空白")
	@Min(value=1, message="房型編號:格式錯誤，請輸入數字")
	private Integer roomTypePhotoId;
	
	@ManyToOne
	@JoinColumn(name = "room_type_id", referencedColumnName = "room_type_id", nullable = false)
	@NotNull(message = "房型編號: 請勿空白")
	@Min(value=1, message="房型編號:格式錯誤，請輸入數字")
	private RoomType roomType; 
	
	@Lob
	@Column(name = "room_type_photo")
	private Byte[] roomTypePhoto;


}
