package com.islevilla.patty.booking.model;

import com.islevilla.lai.members.model.Members;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class BookingEmailServiceImpl implements BookingEmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private TemplateEngine templateEngine;

    @Value("${spring.mail.username}")
    private String from;

    @Override
    public void sendBookingConfirmationEmail(Members member, Booking booking) {
        Context context = new Context();
        context.setVariable("member", member);
        context.setVariable("booking", booking);

        String subject = "訂房成功通知";
        String content = templateEngine.process("email/booking-confirmation.html", context);

        sendHtmlMail(booking.getEmail(), subject, content); // 寄到訂房填寫的信箱
    }

    private void sendHtmlMail(String to, String subject, String content) {
        MimeMessage message = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(content, true);
            mailSender.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
} 