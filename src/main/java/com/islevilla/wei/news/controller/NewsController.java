package com.islevilla.wei.news.controller;

import com.islevilla.wei.news.model.NewsService;
import com.islevilla.wei.news.model.NewsVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class NewsController {

    @Autowired
    private NewsService newsService;

    @GetMapping("/news")
    public String newsList(Model model) {
        List<NewsVO> newsList = newsService.getAll();
        model.addAttribute("newsList", newsList);
        return "front-end/news/newsList";
    }
}