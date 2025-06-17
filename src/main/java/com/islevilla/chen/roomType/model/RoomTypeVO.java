package com.islevilla.chen.roomType.model;

import java.io.Serializable;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;


@Entity // Hibernate：這是一個資料表對應的 Java 類別
@Table(name="room_type") // Hibernate：指定資料表名稱
@Data
public class RoomTypeVO implements Serializable{
	private static final long serialVersionUID = 1L;
	
	@Id // Hibernate：主鍵
	@Column(name="room_type_id") // Hibernate：對應欄位名稱
	@GeneratedValue(strategy = GenerationType.IDENTITY) // Hibernate：MySQL 的 auto_increment 對應寫法
//	@OneToMany(cascade=CascadeType.ALL, fetch=FetchType.LAZY, mappedBy="room_type_id")
	@OrderBy("room_type_id asc")
	@NotBlank(message = "房型編號: 請勿空白")
	@Pattern(regexp="^[(0-9)]$", message="房型編號:格式錯誤，請輸入數字")
	private int roomTypeId;
	
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
	@PositiveOrZero(message ="數量不得為負數")
	@NotBlank(message = "房間數量:請輸入數字")
	@Pattern(regexp = "^[(0-9)]{3}$", message = "房間數量:格式錯誤，請輸入數字")
	private int roomTypeQuantity;
	
	@Column(name="room_type_capacity")
	@PositiveOrZero(message ="數量不得為負數")
	@NotBlank(message = "房間容納人數:請輸入數字")
	@Pattern(regexp = "^[(0-9)]{3}$", message = "房間容納人數:格式錯誤，請輸入數字")
	private int roomTypeCapacity;
	
	@Column(name="room_type_content")
	@NotBlank(message = "房型說明:請勿空白")
	@Size(max=1000 ,message = "房間說明勿超過1000字")
	private String roomTypeContent;
	
	@Column(name="room_type_price")
	@NotBlank(message = "房型價格:請輸入數字")
	@PositiveOrZero(message ="價格不得為負數")
	@Pattern(regexp = "^[(0-9)]$", message = "房型價格:格式錯誤，請輸入數字")
	private int roomTypePrice;
	
	@Column(name="room_type_sale_status") //0:下架中 1:上架中
	//使用下拉選單
	private byte roomTypeSaleStatus;
	
}

