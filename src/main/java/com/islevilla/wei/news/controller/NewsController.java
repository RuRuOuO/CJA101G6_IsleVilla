package com.islevilla.wei.news.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class NewsController {

    @GetMapping("/news")
    public String newsList() {
        // 添加需要的數據
        // model.addAttribute("newsList", newsService.getAllNews());

        return "front-end/news/newsList";
    }
}