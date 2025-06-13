package com.islevilla.wei.news.controller;

import com.islevilla.wei.news.model.NewsService;
import com.islevilla.wei.news.model.News;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@Controller // 標記為 Spring MVC 控制器，可以處理 HTTP 請求並返回視圖
public class NewsController {

    @Autowired
    private NewsService newsService;

    @GetMapping("/news")
    public String newsList(
            // @RequestParam 從網址參數中取值，defaultValue 設定預設值
            @RequestParam(defaultValue = "0") int page,    // 頁碼從 0 開始
            @RequestParam(defaultValue = "9") int size,    // 每頁 9 筆新聞
            Model model) {

        // 建立分頁物件，設定頁碼、每頁筆數、排序方式
        // Sort.by("newsTime").descending() 表示按新聞時間降序排列（最新的在前面）
        Pageable pageable = PageRequest.of(page, size, Sort.by("newsTime").descending());

        // 呼叫 Service 層取得分頁資料
        Page<News> newsPage = newsService.getAll(pageable);

        // 將資料加入到 Model 中，供前端模板使用
        model.addAttribute("newsList", newsPage.getContent());        // 當前頁的新聞資料
        model.addAttribute("currentPage", page);                      // 目前頁碼
        model.addAttribute("totalPages", newsPage.getTotalPages());   // 總頁數
        model.addAttribute("totalItems", newsPage.getTotalElements()); // 總筆數

        // 返回模板路徑，對應到 src/main/resources/templates/front-end/news/newsList.html
        return "front-end/news/listAllNews";
    }

    @GetMapping("/news/{newsId}")
    public String newsDetail(@PathVariable Integer newsId, Model model) {
        // 根據消息 ID 查詢單筆消息資料
        News news = newsService.getById(newsId);

        // 檢查消息是否存在
        if (news == null) {
            // 如果消息不存在，重導向到消息列表頁面
            return "redirect:/news";
        }

        // 將消息資料加入到 Model 中，供前端模板使用
        model.addAttribute("news", news);

        // 返回消息詳情模板路徑
        return "front-end/news/listOneNews";
    }

    @GetMapping("/news/image/{newsId}")
    public ResponseEntity<byte[]> getNewsImage(@PathVariable Integer newsId) {
        // 根據新聞 ID 查詢新聞資料
        News news = newsService.getById(newsId);

        // 檢查新聞是否存在且有圖片資料
        if (news != null && news.getNewsImage() != null) {
            // 取得圖片的二進位資料
            byte[] imageData = news.getNewsImage();

            // 根據圖片資料判斷圖片格式（JPEG、PNG、GIF 等）
            MediaType mediaType = getMediaType(imageData);

            // 如果能成功識別圖片格式
            if (mediaType != null) {
                // 建立成功的 HTTP 響應
                return ResponseEntity.ok()
                        .contentType(mediaType)  // 設定正確的 Content-Type
                        .body(imageData);        // 回傳圖片資料
            }
        }

        // 如果新聞不存在或沒有圖片，回傳 404 Not Found
        return ResponseEntity.notFound().build();
    }

    private MediaType getMediaType(byte[] imageData) {
        // 如果檔案太小（少於 4 個位元組），無法判斷格式
        if (imageData.length < 4) {
            return null;
        }

        // 檢查圖片格式的魔數（Magic Numbers）

        // JPEG 格式：檔頭為 FF D8
        if (imageData[0] == (byte) 0xFF && imageData[1] == (byte) 0xD8) {
            return MediaType.IMAGE_JPEG;
        }

        // PNG 格式：檔頭為 89 50 4E 47 (對應 ASCII 的 ".PNG")
        if (imageData[0] == (byte) 0x89 && imageData[1] == (byte) 0x50 &&
                imageData[2] == (byte) 0x4E && imageData[3] == (byte) 0x47) {
            return MediaType.IMAGE_PNG;
        }

        // GIF 格式：檔頭為 47 49 46 (對應 ASCII 的 "GIF")
        if (imageData[0] == (byte) 0x47 && imageData[1] == (byte) 0x49 &&
                imageData[2] == (byte) 0x46) {
            return MediaType.IMAGE_GIF;
        }

        // 如果無法識別格式，預設回傳 JPEG
        // 這是一個安全的預設值，大多數瀏覽器都支援 JPEG 格式
        return MediaType.IMAGE_JPEG;
    }
}