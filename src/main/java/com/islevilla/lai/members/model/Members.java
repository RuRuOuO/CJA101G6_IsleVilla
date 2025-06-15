package com.islevilla.lai.members.model;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Entity
@Table(name = "members")
@Data
public class Members {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "member_id")
	private Integer memberId;
	
	@Column(name = "member_email")
    @NotEmpty(message = "請輸入電子信箱")
	private String memberEmail;
	
	@Column(name = "member_password_hash")
    @NotEmpty(message = "請輸入password_hash")
	private String memberPasswordHash;
	
	@Column(name = "member_name")
    @NotEmpty(message = "請輸入您的姓名")
	private String memberName;
	
	@Column(name = "member_birthdate")
    @NotEmpty(message = "請輸入您的生日")
	private LocalDate memberBirthdate;
	
	@Column(name = "member_gender")
    @NotEmpty(message = "請輸入您的性別")
	private Integer memberGender;
	
	@Column(name = "member_phone")
    @NotEmpty(message = "請輸入您的電話")
	private String memberPhone;
	
	@Column(name = "member_address")
    @NotEmpty(message = "請輸入您的地址")
	private String memberAddress;
	
	@Lob
	@Column(name = "member_photo", columnDefinition = "LONGBLOB")
	private byte[] memberPhoto;
	
	@Column(name = "member_created_at")
    @NotEmpty(message = "請填入會員建立時間")
	private LocalDateTime memberCreatedAt;
	
	@Column(name = "member_updated_at")
    @NotEmpty(message = "請填入會員更新時間")
	private LocalDateTime memberUpdatedAt;
	
	@Column(name = "member_last_login_time")
    @NotEmpty(message = "請填入最後登入時間")
	private LocalDateTime memberLastLoginTime;
	
	@Column(name = "member_status")
    @NotEmpty(message = "請填入會員狀態")
	private Integer memberStatus;
	
}
