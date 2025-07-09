package com.islevilla.wei.news.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class NewsService {

    @Autowired
    private NewsRepository newsRepository;

    public void addNews(News news) {
        MultipartFile file = news.getUpFiles();
        if (file != null && !file.isEmpty()) {
            try {
                news.setNewsImage(file.getBytes());
            } catch (IOException e) {
                throw new RuntimeException("圖片處理失敗", e);
            }
        }
        news.setNewsTime(LocalDateTime.now());
        newsRepository.save(news);
    }

    // 修改最新消息
    public void updateNews(News news) {
        news.setNewsTime(LocalDateTime.now());
        newsRepository.save(news);
    }

    public Page<News> getAll(Pageable pageable) {
        return newsRepository.findAllByOrderByNewsIdDesc(pageable);
    }

    public Page<News> getPublished(Pageable pageable) {
        return newsRepository.findAllByNewsStatusOrderByNewsIdDesc(1, pageable);
    }

    public List<News> getLatestThreeNews() {
        return newsRepository.findTop3ByNewsStatusOrderByNewsIdDesc(1);
    }

    public News getById(Integer newsId) {
        // findById 回傳 Optional<NewsVO>，避免直接處理 null 值
        Optional<News> news = newsRepository.findById(newsId);
        // 如果找到資料就回傳，找不到就回傳 null
        return news.orElse(null);
    }

    // 更動資料的save方法
    public void save(News news) {
        newsRepository.save(news);
    }
}