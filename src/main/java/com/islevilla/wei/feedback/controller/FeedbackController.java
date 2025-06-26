package com.islevilla.wei.feedback.controller;

import com.islevilla.lai.members.model.Members;
import com.islevilla.wei.feedback.model.Feedback;
import com.islevilla.wei.feedback.model.FeedbackService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class FeedbackController {
    @Autowired
    private FeedbackService feedbackService; // 封裝類別

    // 前台顯示全部feedback
    @GetMapping("/feedback/list")
    public String feedbackList(Model model) {
        List<Feedback> feedbackList = feedbackService.findAll();
        model.addAttribute("feedbackList", feedbackList);
        return "front-end/feedback/listAllFeedback";
    }

    // 前台會員頁面顯示表單
    @GetMapping("/member/feedback/list")
    public String memberFeedbackList(HttpSession session) {
        Members loginMember = (Members) session.getAttribute("member");
        if (loginMember == null) {
            return "redirect:/member/login";
        }
        return "front-end/member/member-feedback-list";
    }

    // 後台顯示全部feedback (thymeleaf版)
    @GetMapping("/backend/thymeleaf/feedback/list")
    public String backThyleafFeedbackList(Model model) {
        List<Feedback> feedbackList = feedbackService.findAll();
        model.addAttribute("feedbackList", feedbackList);
        return "back-end/feedback/listAllFeedback_Thymeleaf";
    }

    // 後台顯示全部feedback
    @GetMapping("/backend/feedback/list")
    public String backFeedbackList() {
        return "back-end/feedback/listAllFeedback";
    }
}
