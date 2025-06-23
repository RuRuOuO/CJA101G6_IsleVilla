package com.islevilla.chen.roomType.model;

import java.util.List;

import org.springframework.stereotype.Component;

import com.islevilla.chen.roomTypePhoto.model.RoomTypePhoto;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;


@Entity // Hibernate：這是一個資料表對應的 Java 類別
@Table(name="room_type") // Hibernate：指定資料表名稱
@Data
@NoArgsConstructor
@AllArgsConstructor
@Component
public class RoomType {
	
	@Id // Hibernate：主鍵
	@Column(name="room_type_id") // Hibernate：對應欄位名稱
	@GeneratedValue(strategy = GenerationType.IDENTITY) // Hibernate：MySQL 的 auto_increment 對應寫法
	@OrderBy("room_type_id asc")
	@NotNull(message = "房型編號: 請勿空白")
	@Min(value=1, message="房型編號:格式錯誤，請輸入數字")
	private Integer roomTypeId;
	
	@Column(name="room_type_code") 
	@NotBlank(message = "房型代碼: 請勿空白")
	@Pattern(regexp = "^[(a-zA-Z)]{3}$", message = "房型代碼: 請輸入英文字母,且長度為3")
	private String roomTypeCode;
	
	@Column(name="room_type_name")
	@Size(max=3)
	@NotBlank(message = "房型名稱: 請勿空白")
	@Pattern(regexp = "^[(\u4e00-\u9fa5)]{1,10}$", message = "房型名稱: 請輸入中文,且長度必需在1到10之間")
	private String roomTypeName;
	
	@Column(name="room_type_quantity")
	@NotNull(message = "房間數量:請輸入數字")
	@Min(value=1, message = "房間數量:格式錯誤，請輸入數字")
	private Integer roomTypeQuantity;
	
	@Column(name="room_type_capacity")
	@NotNull(message = "房間容納人數:請輸入數字")
	@Min(value=1, message = "房間容納人數:格式錯誤，請輸入數字")
	private Integer roomTypeCapacity;
	
	@Column(name="room_type_content")
	@NotBlank(message = "房型說明:請勿空白")
	@Size(max=1000 ,message = "房間說明勿超過1000字")
	private String roomTypeContent;
	
	@Column(name="room_type_price")
	@NotNull(message = "房型價格:請輸入數字")
	@Min(value=1, message = "房型價格:格式錯誤，請輸入數字")
	private Integer roomTypePrice;
	
	@Column(name="room_type_sale_status") //0:下架中 1:上架中
	//使用下拉選單
	private Byte roomTypeSaleStatus;
	
	//一個房型有多張照片
	@OneToMany(mappedBy = "roomType", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@ToString.Exclude
    private List<RoomTypePhoto> roomTypePhotos;

}

