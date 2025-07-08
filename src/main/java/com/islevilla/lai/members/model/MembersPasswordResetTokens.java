package com.islevilla.lai.members.model;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "members_password_reset_tokens")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MembersPasswordResetTokens {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "token_id")
	private Integer tokenId;

	@Column(name = "token", nullable = false, unique = true, length = 255)
	private String token;

	@Column(name = "member_email", nullable = false, length = 100)
	private String memberEmail;

	@Column(name = "expiry_time", nullable = false)
	private LocalDateTime expiryTime;

	@Column(name = "used", nullable = false)
	private Boolean used = false;

	@Column(name = "created_at", nullable = false)
	private LocalDateTime createdAt;

	@PrePersist
	protected void onCreate() {
		createdAt = LocalDateTime.now();
		if (used == null) {
			used = false;
		}
	}

	// 檢查token是否已過期
	public boolean isExpired() {
		return LocalDateTime.now().isAfter(expiryTime);
	}

	// 檢查token是否有效（未使用且未過期）
	public boolean isValid() {
		return !used && !isExpired();
	}
}