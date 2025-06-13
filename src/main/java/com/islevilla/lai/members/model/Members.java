package com.islevilla.lai.members.model;

import java.sql.Date;
import java.sql.Timestamp;

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
	
	@Column(name = "member_password_salt")
    @NotEmpty(message = "請輸入password_salt")
	private String memberPasswordSalt;
	
	@Column(name = "member_name")
    @NotEmpty(message = "請輸入您的姓名")
	private String memberName;
	
	@Column(name = "member_birthdate")
    @NotEmpty(message = "請輸入您的生日")
	private Date memberBirthdate;
	
	@Column(name = "member_gender")
    @NotEmpty(message = "請輸入您的性別")
	private Integer memberGender;
	
	@Column(name = "member_phone")
    @NotEmpty(message = "請輸入您的電話")
	private String memberPhone;
	
	@Column(name = "member_address")
    @NotEmpty(message = "請輸入您的地址")
	private String memberAddress;
	
	@Column(name = "member_photo")
	private byte[] memberPhoto;
	
	@Column(name = "member_created_at")
    @NotEmpty(message = "請填入會員建立時間")
	private Timestamp memberCreatedAt;
	
	@Column(name = "member_updated_at")
    @NotEmpty(message = "請填入會員更新時間")
	private Timestamp memberUpdatedAt;
	
	@Column(name = "member_last_login_time")
    @NotEmpty(message = "請填入最後登入時間")
	private Timestamp memberLastLoginTime;
	
	@Column(name = "member_status")
    @NotEmpty(message = "請填入會員狀態")
	private Integer memberStatus;
	
}
