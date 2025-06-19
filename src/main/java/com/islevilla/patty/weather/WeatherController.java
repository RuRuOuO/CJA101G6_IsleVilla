package com.islevilla.patty.weather;

import org.springframework.stereotype.Controller;

import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WeatherController {
	    
	  @GetMapping("/Weather")
	  public String Weather() {
	        // 添加需要的數據
	        // model.addAttribute("newsList", newsService.getAllNews());
	        
	      return "front-end/customer/weather";
	    }
	}

