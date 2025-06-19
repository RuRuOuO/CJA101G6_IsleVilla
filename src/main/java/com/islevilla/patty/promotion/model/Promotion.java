package com.islevilla.patty.promotion.model;

import java.io.IOException;
import java.sql.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Past;

//import org.hibernate.validator.constraints.NotEmpty;
import jakarta.validation.constraints.NotEmpty;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

/*
 * 註1: classpath必須有javax.persistence-api-x.x.jar 
 * 註2: Annotation可以添加在屬性上，也可以添加在getXxx()方法之上
 */


@Entity  //要加上@Entity才能成為JPA的一個Entity類別
@Table(name = "promotion") //代表這個class是對應到資料庫的實體table，目前對應的table是EMP2 
public class Promotion implements java.io.Serializable {
	private static final long serialVersionUID = 1L;
	
	private Integer roomPromotionId;
	private String roomPromotionTitle;
	private Date promotionStartDate;
	private Date promotionEndDate;
	private String promotionRemark;
	

	public Promotion() { //必需有一個不傳參數建構子(JavaBean基本知識)
	}

	@Id //@Id代表這個屬性是這個Entity的唯一識別屬性，並且對映到Table的主鍵 
	@Column(name = "promotion")  //@Column指這個屬性是對應到資料庫Table的哪一個欄位   //【非必要，但當欄位名稱與屬性名稱不同時則一定要用】
	@GeneratedValue(strategy = GenerationType.IDENTITY) //@GeneratedValue的generator屬性指定要用哪個generator //【strategy的GenerationType, 有四種值: AUTO, IDENTITY, SEQUENCE, TABLE】 
	public Integer getRoomPromotionId() {
		return this.roomPromotionId;
	}

	public void setRoomPromotionId(Integer roomPromotionId) {
		this.roomPromotionId = roomPromotionId;
	}

	@Column(name = "room_promotion_title")
	@NotEmpty(message="優惠專案名稱: 請勿空白")
	@Pattern(regexp = "^[(\u4e00-\u9fa5)(a-zA-Z0-9)]{2,10}$", message = "優惠專案名稱: 只能是中、英文字母、數字 , 且長度必需在2到10之間")
	public String getRoomPromotionTitle() {
		return this.roomPromotionTitle;
	}

	public void setRoomPromotionTitle(String roomPromotionTitle) {
		this.roomPromotionTitle = roomPromotionTitle;
	}
	
	@Column(name = "promotion_start_date")
//	@NotNull(message="雇用日期: 請勿空白")	
//	@Future(message="日期必須是在今日(不含)之後")
//	@Past(message="日期必須是在今日(含)之前")
//	@DateTimeFormat(pattern="yyyy-MM-dd") 
//	@DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss") 
	public Date PromotionStartDate() {
		return this.promotionStartDate;
	}

	public void setPromotionStartDate(Date promotionStartDate) {
		this.promotionStartDate = promotionStartDate;
	}
	
	@Column(name = "promotion_end_date")
//	@NotNull(message="雇用日期: 請勿空白")	
//	@Future(message="日期必須是在今日(不含)之後")
//	@Past(message="日期必須是在今日(含)之前")
//	@DateTimeFormat(pattern="yyyy-MM-dd") 
//	@DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss") 
	public Date PromotionEndDate() {
		return this.promotionEndDate;
	}

	public void setPromotionEndDate(Date promotionEndDate) {
		this.promotionEndDate = promotionEndDate;
	}
	
	@Column(name = "promotion_remark")
	@Pattern(regexp = "^[(\u4e00-\u9fa5)(a-zA-Z0-9)]{2,10}$", message = "優惠專案名稱: 只能是中、英文字母、數字 , 且長度必需在2到10之間")
	public String getPromotionRemark() {
		return this.promotionRemark;
	}

	public void setPromotionRemark(String promotionRemark) {
		this.promotionRemark = promotionRemark;
	}
}
