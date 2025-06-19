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

/**
 * 新聞服務類別 - 負責處理新聞相關的業務邏輯
 * 這是一個 Service Layer，位於 Controller 和 Repository 之間
 * 主要功能：封裝業務邏輯、處理資料驗證、協調多個 Repository 操作
 */
@Service
public class NewsService {

    /**
     * 注入新聞資料庫操作介面
     *
     * @Autowired 告訴 Spring 自動尋找 NewsRepository 類型的 Bean 並注入到此變數
     * 這樣就可以透過 newsRepository 來執行資料庫的 CRUD 操作
     */
    @Autowired
    private NewsRepository newsRepository;

    /**
     * 新增新聞方法
     *
     * @param news 要新增的新聞物件，包含新聞的所有資訊
     *             <p>
     *             功能說明：
     *             - 接收前端傳來的新聞資料
     *             - 呼叫 Repository 的 save() 方法將資料存入資料庫
     *             - JPA 的 save() 方法會自動判斷是新增還是更新（根據主鍵是否存在）
     */
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

    /**
     * 分頁查詢所有新聞方法
     *
     * @param pageable 分頁參數物件，包含頁碼、每頁筆數、排序方式等資訊
     * @return Page<NewsVO> 分頁結果物件，包含當前頁資料、總頁數、總筆數等資訊
     * <p>
     * 功能說明：
     * - 支援分頁查詢，避免一次載入太多資料造成效能問題
     * - Page 物件不只包含資料，還有分頁相關的元資訊
     * - 前端可以根據 Page 物件的資訊來產生分頁導航
     */
    public Page<News> getAll(Pageable pageable) {
        return newsRepository.findAll(pageable);
    }

    public Page<News> getPublished(Pageable pageable) {
        return newsRepository.findAllByNewsStatus(1, pageable);
    }

    public List<News> getLatestThreeNews() {
        return newsRepository.findTop3ByNewsStatusOrderByNewsTimeDesc(1);
    }

    /**
     * 根據 ID 查詢單筆新聞方法
     *
     * @param newsId 要查詢的新聞 ID
     * @return NewsVO 查詢到的新聞物件，如果找不到則回傳 null
     * <p>
     * 功能說明：
     * - JPA 的 findById() 方法回傳 Optional<NewsVO> 物件，這是 Java 8 的特性
     * - Optional 可以避免 NullPointerException，提供更安全的程式設計方式
     * - orElse(null) 表示如果 Optional 中沒有值，就回傳 null
     * - 也可以用 orElseThrow() 來拋出自訂例外，或用 orElse(new NewsVO()) 回傳預設物件
     */
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