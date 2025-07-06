package com.islevilla.lai.email.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.islevilla.lai.email.model.EmailService;

@Controller
public class EmailController {
    
    @Autowired
    private EmailService emailService;
    
    @GetMapping("/send-email")
    public String showEmailForm() {
        return "email-form";
    }
    
    @PostMapping("/send-email")
    public String sendEmail(
            @RequestParam String to,
            @RequestParam String subject,
            @RequestParam String message,
            RedirectAttributes redirectAttributes) {
        
        try {
            emailService.sendSimpleEmail(to, subject, message);
            redirectAttributes.addFlashAttribute("success", "郵件發送成功！");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "郵件發送失敗：" + e.getMessage());
        }
        
        return "redirect:/send-email";
    }
}
