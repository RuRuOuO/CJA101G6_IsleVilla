package com.islevilla.wei;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Page;
import org.springframework.ui.Model;

public class PageUtil {
    // pageResult: 分頁的結果, model: 要塞值得Model, currentPage: 目前頁碼, attributeName: List的model名稱
    public static <T> void ModelWithPage(Page<T> pageResult, Model model, int currentPage, String attributeName, HttpServletRequest request) {
        model.addAttribute(attributeName, pageResult.getContent());
        model.addAttribute("currentPage", currentPage);
        model.addAttribute("totalPages", pageResult.getTotalPages());
        model.addAttribute("totalItems", pageResult.getTotalElements());
        String pageURL = request.getRequestURI(); // ✅ 在這裡取得 URI
        model.addAttribute("pageURL", pageURL);   // ✅ 加到 model 裡
    }
}
