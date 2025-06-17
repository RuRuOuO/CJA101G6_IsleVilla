package com.islevilla.patty.customerservice;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class FaqController {
    
    @GetMapping("/faq")
    public String faq() {
        // 添加需要的數據
        // model.addAttribute("newsList", newsService.getAllNews());
        
        return "front-end/customer/faq";
    }
}
