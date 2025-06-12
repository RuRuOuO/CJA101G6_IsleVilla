package com.islevilla.wei.news.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

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
        if(newsRepository.existsById(newsId)){
            newsRepository.deleteById(newsId);
        }
    }
    public List<NewsVO> getAll() {
        return newsRepository.findAll();
    }
}
