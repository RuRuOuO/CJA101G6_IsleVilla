package com.islevilla.wei.news.controller;

import com.islevilla.wei.news.model.NewsService;
import com.islevilla.wei.news.model.NewsVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/front-end/news")
public class NewsController {

    @Autowired
    private NewsService newsService;

    /**
     * 顯示新聞列表頁面
     * 對應模板：src/main/resources/templates/front-end/news/newsList.html
     * 訪問網址：http://localhost:8080/front-end/news/newsList
     */
    @GetMapping("/newsList")
    public String newsList(Model model) {
        // 從 Service 取得所有新聞資料
        List<NewsVO> newsList = newsService.getAll();

        // 將新聞列表加入到 model 中，供前端模板使用
        model.addAttribute("newsList", newsList);

        // 返回模板路徑
        return "front-end/news/newsList";
    }
}