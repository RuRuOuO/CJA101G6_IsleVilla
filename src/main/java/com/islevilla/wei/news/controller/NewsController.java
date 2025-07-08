package com.islevilla.wei.news.controller;

import com.islevilla.patty.promotion.model.Promotion;
import com.islevilla.patty.promotion.model.PromotionService;
import com.islevilla.patty.roompromotionprice.model.RoomPromotionPrice;
import com.islevilla.wei.PageUtil;
import com.islevilla.wei.news.model.News;
import com.islevilla.wei.news.model.NewsService;
import com.islevilla.wei.room.model.WeiRoomPromotionPriceService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Controller // 標記為 Spring MVC 控制器，可以處理 HTTP 請求並返回視圖
public class NewsController {

    @Autowired
    private NewsService newsService;

    @Autowired
    private PromotionService promotionService;

    @Autowired
    private WeiRoomPromotionPriceService weiRoomPromotionPriceService;

    // 前台顯示全部消息
    @GetMapping("/news/list")
    public String newsList(
            // @RequestParam 從網址參數中取值，defaultValue 設定預設值
            @RequestParam(defaultValue = "0") int page,    // 頁碼從 0 開始
            @RequestParam(defaultValue = "9") int size,    // 每頁 9 筆新聞
            Model model,
            HttpServletRequest request) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("newsTime").descending());
        Page<News> newsPage = newsService.getPublished(pageable);

        PageUtil.ModelWithPage(newsPage, model, page, "newsList", request);
        // 返回模板路徑，對應到 src/main/resources/templates/front-end/news/newsList.html
        return "front-end/news/listAllNews";
    }

    // 後台顯示全部消息
    @GetMapping("/backend/news/list")
    public String backNewsList(
            // @RequestParam 從網址參數中取值，defaultValue 設定預設值
            @RequestParam(defaultValue = "0") int page,    // 頁碼從 0 開始
            @RequestParam(defaultValue = "9") int size,    // 每頁 9 筆新聞
            Model model,
            HttpServletRequest request) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("newsTime").descending());
        Page<News> newsPage = newsService.getAll(pageable);

        PageUtil.ModelWithPage(newsPage, model, page, "newsList", request);
        model.addAttribute("sidebarActive", "news-list");
        // 返回模板路徑，對應到 src/main/resources/templates/front-end/news/newsList.html
        return "back-end/news/listAllNews";
    }

    // 新增消息頁面
    @GetMapping("/backend/news/add")
    public String addNewsPage(Model model) { // model: spring自動建立的物件，用來傳遞資料
        model.addAttribute("news", new News());
        model.addAttribute("promotionList", promotionService.getAll());
        return "back-end/news/addNews";
    }

    // 新增消息
    @PostMapping("/backend/news/add")
    public String addNews(@Valid @ModelAttribute("news") News news,
                          BindingResult result,
                          Model model) {

        // 表單驗證失敗：回填promotionList讓下拉選單能正確顯示
        if (result.hasErrors()) {
            model.addAttribute("promotionList", promotionService.getAll());
            return "back-end/news/addNews";
        }

        //　如果有設定 Promotion，就從資料庫抓取實體
        if (news.getPromotion() != null && news.getPromotion().getRoomPromotionId() != null) {
            Promotion promotion = promotionService.getOnePromotion(news.getPromotion().getRoomPromotionId());
            news.setPromotion(promotion);
        } else {
            news.setPromotion(null); // 沒有選擇就設為 null
        }

        newsService.addNews(news);
        return "redirect:/backend/news/list";
    }

    // 更新
    @GetMapping("/backend/news/edit/{id}")
    public String editNews(@PathVariable("id") Integer id, Model model) {
        News news = newsService.getById(id); // 假設這方法存在
        model.addAttribute("news", news);
        model.addAttribute("promotionList", promotionService.getAll());
        return "back-end/news/update_news_input"; // 指向你要渲染的編輯畫面
    }

    @PostMapping("/backend/news/update")
    public String updateNews(
            @ModelAttribute("news") News news,
            @RequestParam("newsImageFile") MultipartFile imageFile) {
        // 避免 TransientObjectException
        if (news.getPromotion() != null && news.getPromotion().getRoomPromotionId() != null) {
            Promotion promotion = promotionService.getOnePromotion(news.getPromotion().getRoomPromotionId());
            news.setPromotion(promotion);
        } else {
            news.setPromotion(null); // 沒有選擇優惠專案就設為 null
        }

        // 取得原始資料（含原圖）
        News originalNews = newsService.getById(news.getNewsId());

        // 若使用者沒有上傳新圖片，就保留原圖片
        if (imageFile == null || imageFile.isEmpty()) {
            news.setNewsImage(originalNews.getNewsImage());
        } else {
            try {
                news.setNewsImage(imageFile.getBytes());
            } catch (IOException e) {
                e.printStackTrace();
                // 若出錯，也保留原圖
                news.setNewsImage(originalNews.getNewsImage());
            }
        }

        newsService.updateNews(news);
        return "redirect:/backend/news/list";
    }

    @GetMapping("/news/{newsId}")
    public String newsDetail(@PathVariable Integer newsId, Model model) {
        // 根據消息 ID 查詢單筆消息資料
        News news = newsService.getById(newsId);

        // 檢查消息是否存在
        if (news == null) {
            // 如果消息不存在，重導向到消息列表頁面
            return "redirect:/news/list";
        }

        // 將消息資料加入到 Model 中，供前端模板使用
        model.addAttribute("news", news);

        // 取得對應優惠專案資訊
        if (news.getPromotion() != null) {
            List<RoomPromotionPrice> promotionPrices = weiRoomPromotionPriceService.getByPromotionId(news.getPromotion().getRoomPromotionId());
            model.addAttribute("promotionPrices", promotionPrices);
        }

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
                return ResponseEntity.ok().contentType(mediaType)  // 設定正確的 Content-Type
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
        if (imageData[0] == (byte) 0x89 && imageData[1] == (byte) 0x50 && imageData[2] == (byte) 0x4E && imageData[3] == (byte) 0x47) {
            return MediaType.IMAGE_PNG;
        }

        // GIF 格式：檔頭為 47 49 46 (對應 ASCII 的 "GIF")
        if (imageData[0] == (byte) 0x47 && imageData[1] == (byte) 0x49 && imageData[2] == (byte) 0x46) {
            return MediaType.IMAGE_GIF;
        }

        // 如果無法識別格式，預設回傳 JPEG
        // 這是一個安全的預設值，大多數瀏覽器都支援 JPEG 格式
        return MediaType.IMAGE_JPEG;
    }
}