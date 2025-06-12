package com.islevilla.wei.news.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class NewsService {
    @Autowired // Spring找到EmpRepository類型的Bean並注入
    private NewsRepository newsRepository;

    public void addNews(NewsVO newsVO) {
        newsRepository.save(newsVO);
    }

    public void updateNews(NewsVO newsVO) {
        newsRepository.save(newsVO);
    }

    public void deleteNews(Integer newsId) {
        if (newsRepository.existsById(newsId)) {
            newsRepository.deleteById(newsId);
        }
    }

    public Page<NewsVO> getAll(Pageable pageable) {
        return newsRepository.findAll(pageable);
    }

    public NewsVO getById(Integer newsId) {
        Optional<NewsVO> news = newsRepository.findById(newsId);
        return news.orElse(null);
    }
}
