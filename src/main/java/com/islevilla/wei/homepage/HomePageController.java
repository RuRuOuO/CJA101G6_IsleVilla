package com.islevilla.wei.homepage;

import com.islevilla.wei.news.model.News;
import com.islevilla.wei.news.model.NewsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class HomePageController {

    @Autowired
    private NewsService newsService;

    @GetMapping("/")
    public String index(Model model) {
        List<News> latest3News = newsService.getLatestThreeNews();
        model.addAttribute("latestNews", latest3News); // ✅ 有加這行才不會報錯
        return "front-end/index";
    }

//    @GetMapping("/member")
//    public String member() {
//        return "front-end/member/memberInfo";
//    }
}