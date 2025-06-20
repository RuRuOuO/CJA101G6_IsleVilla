//package com.islevilla.patty.roompromotionprice.model;
//
//import java.io.IOException;
//import java.sql.Date;
//
//import jakarta.persistence.Entity;
//import jakarta.persistence.GeneratedValue;
//import jakarta.persistence.GenerationType;
//import jakarta.persistence.Id;
//import jakarta.persistence.Column;
//import jakarta.persistence.JoinColumn;
//import jakarta.persistence.ManyToOne;
//import jakarta.persistence.SequenceGenerator;
//import jakarta.persistence.Table;
//import jakarta.persistence.Temporal;
//import jakarta.persistence.TemporalType;
//
//import jakarta.validation.constraints.NotNull;
//import jakarta.validation.constraints.Pattern;
//import jakarta.validation.constraints.Size;
//import jakarta.validation.constraints.DecimalMin;
//import jakarta.validation.Valid;
//import jakarta.validation.constraints.DecimalMax;
//import jakarta.validation.constraints.Future;
//import jakarta.validation.constraints.Past;
//
////import org.hibernate.validator.constraints.NotEmpty;
//import jakarta.validation.constraints.NotEmpty;
//
//import org.springframework.format.annotation.DateTimeFormat;
//import org.springframework.web.multipart.MultipartFile;
//
///*
// * 註1: classpath必須有javax.persistence-api-x.x.jar 
// * 註2: Annotation可以添加在屬性上，也可以添加在getXxx()方法之上
// */
//
//
//@Entity  //要加上@Entity才能成為JPA的一個Entity類別
//@Table(name = "RoomPromotionPrice") //代表這個class是對應到資料庫的實體table，目前對應的table是EMP2 
//public class RoomPromotionPrice implements java.io.Serializable {
//	private static final long serialVersionUID = 1L;
//	
//	private Integer roomPromotionId;
//	private Integer roomTypeId;
//	private Integer roomDiscountPrice;
//	
//
//	public RoomPromotionPrice() { //必需有一個不傳參數建構子(JavaBean基本知識)
//	}
//
//	@Id //@Id代表這個屬性是這個Entity的唯一識別屬性，並且對映到Table的主鍵 
//	@Column(name = "RoomPromotionId")  //@Column指這個屬性是對應到資料庫Table的哪一個欄位   //【非必要，但當欄位名稱與屬性名稱不同時則一定要用】
//	@GeneratedValue(strategy = GenerationType.IDENTITY) //@GeneratedValue的generator屬性指定要用哪個generator //【strategy的GenerationType, 有四種值: AUTO, IDENTITY, SEQUENCE, TABLE】 
//	public Integer getRoomPromotionId() {
//		return this.roomPromotionId;
//	}
//
//	public void setRoomPromotionId(Integer roomPromotionId) {
//		this.roomPromotionId = roomPromotionId;
//	}
//
//	// @ManyToOne  (雙向多對一/一對多) 的多對一
//	//【此處預設為 @ManyToOne(fetch=FetchType.EAGER) --> 是指 lazy="false"之意】【注意: 此處的預設值與XML版 (p.127及p.132) 的預設值相反】
//	//【如果修改為 @ManyToOne(fetch=FetchType.LAZY)  --> 則指 lazy="true" 之意】
//	@ManyToOne
//	@JoinColumn(name = "RoomTypeId")   // 指定用來join table的column
//	public RoomType getRoomType() {
//		return this.roomType;
//	}
//
//	public void setRoomType(RoomType roomType) {
//		this.roomType = roomType;
//	}
//
//	@Column(name = "RoomDiscountPrice")
//	@NotEmpty(message="優惠價格: 請勿空白")
//	public Integer getRoomDiscountPrice() {
//		return this.RoomDiscountPrice;
//	}
//
//	public void setRoomDiscountPrice(Integer RoomDiscountPrice) {
//		this.RoomDiscountPrice = RoomDiscountPrice;
//	}
//	
//}
