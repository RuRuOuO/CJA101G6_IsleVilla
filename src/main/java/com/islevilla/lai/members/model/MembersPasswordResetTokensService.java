package com.islevilla.lai.members.model;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.islevilla.lai.email.model.MembersEmailService;
import com.islevilla.lai.util.PasswordConvert;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@Transactional
public class MembersPasswordResetTokensService {
	@Autowired
	private MembersRepository membersRepository;

	@Autowired
	private MembersPasswordResetTokensRepository tokenRepository;

	@Autowired
	private MembersEmailService emailService;

	@Autowired
	private PasswordConvert pc;

	@Value("${app.domain:http://islevilla.ddns.net}")
	private String appDomain;

	/**
	 * 處理忘記密碼請求
	 * 
	 * @param email 會員email
	 * @return 是否成功發送重設郵件
	 */
	public boolean processForgotPassword(String email) {
		try {
			// 檢查會員是否存在
			Optional<Members> memberOpt = membersRepository.findByMemberEmail(email);
			if (memberOpt.isEmpty()) {
				log.warn("嘗試重設不存在的會員密碼: {}", email);
				return false;
			}

			Members member = memberOpt.get();

			// 檢查會員狀態
			if (member.getMemberStatus() == 2) { // 停用狀態
				log.warn("嘗試重設已停用會員的密碼: {}", email);
				return false;
			}

			// 將該會員的所有現有token標記為已使用
			tokenRepository.invalidateAllTokensByEmail(email);

			// 生成新的重設token
			String resetToken = generateResetToken();

			// 建立token記錄（24小時有效期）
			MembersPasswordResetTokens token = new MembersPasswordResetTokens();
			token.setToken(resetToken);
			token.setMemberEmail(email);
			token.setExpiryTime(LocalDateTime.now().plusHours(24));
			token.setUsed(false);

			tokenRepository.save(token);

			// 建立重設密碼的URL
			String resetUrl = appDomain + "/member/reset-password?token=" + resetToken;

			// 建立重設密碼的URL
			String resetUrl_localhost = "http://localhost:8080/member/reset-password?token=" + resetToken;

			// 發送重設郵件
			emailService.sendPasswordResetEmail(email, member.getMemberName(), resetToken, resetUrl,
					resetUrl_localhost);

			log.info("密碼重設請求處理成功: {}", email);
			return true;

		} catch (Exception e) {
			log.error("處理忘記密碼請求失敗: {}", e.getMessage(), e);
			return false;
		}
	}

	/**
	 * 驗證重設token
	 * 
	 * @param token 重設token
	 * @return token是否有效
	 */
	public boolean validateResetToken(String token) {
		Optional<MembersPasswordResetTokens> tokenOpt = tokenRepository.findByToken(token);
		return tokenOpt.map(MembersPasswordResetTokens::isValid).orElse(false);
	}

	/**
	 * 根據token獲取會員email
	 * 
	 * @param token 重設token
	 * @return 會員email
	 */
	public String getEmailByToken(String token) {
		return tokenRepository.findByToken(token).map(MembersPasswordResetTokens::getMemberEmail).orElse(null);
	}

	/**
	 * 重設密碼
	 * 
	 * @param token       重設token
	 * @param newPassword 新密碼
	 * @return 是否成功重設
	 */
	public boolean resetPassword(String token, String newPassword) {
		try {
			// 驗證token
			Optional<MembersPasswordResetTokens> tokenOpt = tokenRepository.findByToken(token);
			if (tokenOpt.isEmpty() || !tokenOpt.get().isValid()) {
				log.warn("使用無效token嘗試重設密碼: {}", token);
				return false;
			}

			MembersPasswordResetTokens resetToken = tokenOpt.get();
			String email = resetToken.getMemberEmail();

			// 獲取會員
			Optional<Members> memberOpt = membersRepository.findByMemberEmail(email);
			if (memberOpt.isEmpty()) {
				log.warn("Token對應的會員不存在: {}", email);
				return false;
			}

			Members member = memberOpt.get();

			// 更新密碼
			member.setMemberPasswordHash(pc.hashing(newPassword));
			member.setMemberUpdatedAt(LocalDateTime.now());
			membersRepository.save(member);

			// 標記token為已使用
			resetToken.setUsed(true);
			tokenRepository.save(resetToken);

			// 將該會員的所有其他token標記為已使用
			tokenRepository.invalidateAllTokensByEmail(email);

			// 發送密碼重設成功通知
			emailService.sendPasswordResetSuccessEmail(email, member.getMemberName());

			log.info("密碼重設成功: {}", email);
			return true;

		} catch (Exception e) {
			log.error("重設密碼失敗: {}", e.getMessage(), e);
			return false;
		}
	}

	/**
	 * 清理過期的token
	 */
	public void cleanupExpiredTokens() {
		try {
			tokenRepository.deleteExpiredTokens(LocalDateTime.now());
			log.info("過期token清理完成");
		} catch (Exception e) {
			log.error("清理過期token失敗: {}", e.getMessage(), e);
		}
	}

	/**
	 * 生成重設token
	 * 
	 * @return 唯一的重設token
	 */
	private String generateResetToken() {
		return UUID.randomUUID().toString();
	}
}
