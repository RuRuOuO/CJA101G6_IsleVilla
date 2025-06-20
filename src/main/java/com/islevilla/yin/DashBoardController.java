package com.islevilla.yin;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashBoardController {

    @GetMapping("/backend/dashboard")
    public String dashboard() {
        return "back-end/dashboard";
    }

}
