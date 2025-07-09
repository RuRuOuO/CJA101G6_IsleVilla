package com.islevilla.wei.feedback.model;

import com.islevilla.jay.coupon.model.Coupon;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.List;

@Service
public class FeedbackEmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private TemplateEngine templateEngine;

    public void sendThankYouEmail(String to, String memberName, List<Coupon> coupons) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setFrom("no-reply@islevilla.com");
        helper.setTo(to);
        helper.setSubject("感謝您的問卷回饋！專屬優惠券已送達");

        Context context = new Context();
        context.setVariable("memberName", memberName);
        context.setVariable("coupons", coupons);

        String htmlContent = templateEngine.process("email/email-feedback-thankyou", context);
        helper.setText(htmlContent, true);

        mailSender.send(message);
    }
}