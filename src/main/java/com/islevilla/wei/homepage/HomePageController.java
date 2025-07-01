package com.islevilla.wei.homepage;

import com.islevilla.lai.members.model.Members;
import com.islevilla.wei.feedback.model.Feedback;
import com.islevilla.wei.feedback.model.FeedbackService;
import com.islevilla.wei.news.model.News;
import com.islevilla.wei.news.model.NewsService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class HomePageController {

    @Autowired
    private NewsService newsService;
    @Autowired
    FeedbackService feedbackService;

    @GetMapping("/")
    public String index(Model model) {
        List<News> latest3News = newsService.getLatestThreeNews();
        model.addAttribute("latestNews", latest3News); // ✅ 有加這行才不會報錯
        return "front-end/index";
    }

    @GetMapping("/member")
    public String member(Model model, HttpSession session) {
        Members loginMember = (Members) session.getAttribute("member");
        model.addAttribute("loginMember", loginMember);
        return "front-end/member/member";
    }

//    @GetMapping("/feedback/list")
//    public String member(Model model) {
//        List<Feedback> feedbackList = feedbackService.findAll();
//        model.addAttribute("feedbackList", feedbackList);
//        return "front-end/feedback/listAllFeedback";
//    }

//    @GetMapping("/member")
//    public String member() {
//        return "front-end/member/memberInfo";
//    }
}