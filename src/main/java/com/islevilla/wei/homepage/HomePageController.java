package com.islevilla.wei.homepage;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomePageController {
        @GetMapping("/")
        public String index(){
            return "front-end/index";
        }
}