package com.islevilla.lai.email.model;

import java.nio.charset.StandardCharsets;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class MembersEmailService {

	@Autowired
	private JavaMailSender mailSender;

	@Autowired
	private TemplateEngine templateEngine;

	/**
	 * 發送密碼重設郵件
	 */
	public void sendPasswordResetEmail(String to, String memberName, String resetToken, String resetUrl,
			String resetUrl_localhost) {
		try {
			MimeMessage message = mailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
					StandardCharsets.UTF_8.name());

			// 設定郵件基本資訊
			helper.setTo(to);
			helper.setSubject("【微嶼IsleVilla】密碼重設通知");
			helper.setFrom("noreply@islevilla.com");

			// 建立 Thymeleaf 上下文
			Context context = new Context();
			context.setVariable("memberName", memberName);
			context.setVariable("resetToken", resetToken);
			context.setVariable("resetUrl", resetUrl);
			context.setVariable("resetUrlLocalhost", resetUrl_localhost);
			context.setVariable("validityHours", 24); // token有效期24小時

			// 渲染郵件模板
			String htmlContent = templateEngine.process("email/email-member-password-reset", context);
			helper.setText(htmlContent, true);

			// 發送郵件
			mailSender.send(message);
			log.info("密碼重設郵件已發送至: {}", to);

		} catch (Exception e) {
			log.error("發送密碼重設郵件失敗: {}", e.getMessage(), e);
			throw new RuntimeException("郵件發送失敗", e);
		}
	}

	/**
	 * 發送密碼重設成功通知郵件
	 */
	public void sendPasswordResetSuccessEmail(String to, String memberName) {
		try {
			MimeMessage message = mailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
					StandardCharsets.UTF_8.name());

			helper.setTo(to);
			helper.setSubject("【微嶼IsleVilla】密碼重設成功通知");
			helper.setFrom("noreply@islevilla.com");

			Context context = new Context();
			context.setVariable("memberName", memberName);

			String htmlContent = templateEngine.process("email/email-member-password-reset-success", context);
			helper.setText(htmlContent, true);

			mailSender.send(message);
			log.info("密碼重設成功通知郵件已發送至: {}", to);

		} catch (Exception e) {
			log.error("發送密碼重設成功通知郵件失敗: {}", e.getMessage(), e);
			throw new RuntimeException("郵件發送失敗", e);
		}
	}
}