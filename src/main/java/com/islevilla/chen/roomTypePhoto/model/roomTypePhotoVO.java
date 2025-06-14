package com.islevilla.chen.roomTypePhoto.model;

import org.springframework.stereotype.Component;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "room_type_photo")
@Data
@Component
public class roomTypePhotoVO {
	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name = "room_type_photo_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int roomTypePhotoId;
	
	@Column(name = "room_type_id") //FK
	private int roomTypeId;
	
	@Column(name = "room_type_photo")
	private byte[] roomTypePhoto;


}
