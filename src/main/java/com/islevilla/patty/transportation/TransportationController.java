package com.islevilla.patty.transportation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;



@Controller
public class TransportationController {
	    
	    @GetMapping("/transportation")
	    public String transportation() {
	        // 添加需要的數據
	        // model.addAttribute("newsList", newsService.getAllNews());
	        
	        return "front-end/customer/transportation";

	    }
	}

