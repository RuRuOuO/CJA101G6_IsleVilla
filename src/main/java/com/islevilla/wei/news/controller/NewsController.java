package com.islevilla.wei.news.controller;

import com.islevilla.wei.news.model.NewsService;
import com.islevilla.wei.news.model.NewsVO;
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

@Controller
public class NewsController {

    @Autowired
    private NewsService newsService;

    @GetMapping("/news")
    public String newsList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "9") int size,
            Model model) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("newsTime").descending());
        Page<NewsVO> newsPage = newsService.getAll(pageable);

        model.addAttribute("newsList", newsPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", newsPage.getTotalPages());
        model.addAttribute("totalItems", newsPage.getTotalElements());

        return "front-end/news/newsList";
    }

    @GetMapping("/news/image/{newsId}")
    public ResponseEntity<byte[]> getNewsImage(@PathVariable Integer newsId) {
        NewsVO news = newsService.getById(newsId);
        if (news != null && news.getNewsImage() != null) {
            byte[] imageData = news.getNewsImage();
            MediaType mediaType = getMediaType(imageData);
            if (mediaType != null) {
                return ResponseEntity.ok()
                        .contentType(mediaType)
                        .body(imageData);
            }
        }
        return ResponseEntity.notFound().build();
    }

    private MediaType getMediaType(byte[] imageData) {
        if (imageData.length < 4) {
            return null;
        }

        // 檢查圖片格式的魔數（Magic Numbers）
        if (imageData[0] == (byte) 0xFF && imageData[1] == (byte) 0xD8) {
            return MediaType.IMAGE_JPEG;
        }
        if (imageData[0] == (byte) 0x89 && imageData[1] == (byte) 0x50 &&
                imageData[2] == (byte) 0x4E && imageData[3] == (byte) 0x47) {
            return MediaType.IMAGE_PNG;
        }
        if (imageData[0] == (byte) 0x47 && imageData[1] == (byte) 0x49 &&
                imageData[2] == (byte) 0x46) {
            return MediaType.IMAGE_GIF;
        }
        if (imageData[0] == (byte) 0x42 && imageData[1] == (byte) 0x4D) {
            return MediaType.parseMediaType("image/bmp");
        }
        if (imageData[0] == (byte) 0x52 && imageData[1] == (byte) 0x49 &&
                imageData[2] == (byte) 0x46 && imageData[3] == (byte) 0x46) {
            return MediaType.parseMediaType("image/webp");
        }

        // 如果無法識別格式，預設返回 JPEG
        return MediaType.IMAGE_JPEG;
    }
}