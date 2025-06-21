package com.islevilla.lai.members.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.islevilla.lai.shuttle.model.ShuttleReservation;
import com.islevilla.wei.room.model.RoomRVOrder;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.ToString;

@Entity
@Table(name = "members")
@Data
public class Members {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "member_id")
	private Integer memberId;

	@Column(name = "member_email", nullable = false, unique = true, length = 100)
	@NotBlank(message = "請輸入電子信箱")
	@Email(message = "請輸入有效的電子信箱格式")
	private String memberEmail;

	@Column(name = "member_password_hash")
	@NotBlank(message = "請輸入密碼")
	private String memberPasswordHash;

	@Column(name = "member_name", nullable = false, length = 30)
	@NotBlank(message = "請輸入您的姓名")
	@Size(max = 30, message = "姓名長度不能超過30個字")
	private String memberName;

	@Column(name = "member_birthdate", nullable = false)
	@NotNull(message = "請選擇您的生日")
	private LocalDate memberBirthdate;

	@Column(name = "member_gender", nullable = false)
	@NotNull(message = "請選擇您的性別")
	private Integer memberGender; // 0:男生 1:女生 2:其它

	@Column(name = "member_phone", nullable = false, unique = true, length = 20)
	@NotBlank(message = "請輸入您的電話")
	@Size(max = 20, message = "電話號碼長度不能超過20個字")
	private String memberPhone;

	@Column(name = "member_address", nullable = false, length = 200)
	@NotBlank(message = "請輸入您的地址")
	@Size(max = 200, message = "地址長度不能超過200個字")
	private String memberAddress;

	@Lob
	@Column(name = "member_photo", columnDefinition = "LONGBLOB")
	private byte[] memberPhoto;

	@Column(name = "member_created_at", nullable = false)
	private LocalDateTime memberCreatedAt;

	@Column(name = "member_updated_at", nullable = false)
	private LocalDateTime memberUpdatedAt;

	@Column(name = "member_last_login_time", nullable = false)
	private LocalDateTime memberLastLoginTime;

	@Column(name = "member_status", nullable = false)
	private Integer memberStatus = 0; // 0:未驗證 1:已驗證 2:停用

	// 一對多：一個會員有多個訂房訂單
	@OneToMany(mappedBy = "members", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@ToString.Exclude // 排除此屬性避免循環引用
	private List<RoomRVOrder> roomRVOrder;
	
	// 一對多：一個會員有多個接駁預約
	@OneToMany(mappedBy = "members", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@ToString.Exclude // 排除此屬性避免循環引用
	private List<ShuttleReservation> shuttleReservation;
	
	
	
	
	
	@PrePersist
	protected void onCreate() {
		LocalDateTime now = LocalDateTime.now();
		memberCreatedAt = now;
		memberUpdatedAt = now;
		memberLastLoginTime = now;
		if (memberStatus == null) {
			memberStatus = 0;
		}
	}

	@PreUpdate
	protected void onUpdate() {
		memberUpdatedAt = LocalDateTime.now();
	}
}
