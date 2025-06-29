package com.islevilla.yin.auth;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/backend")
public class AuthController {

    // 登入頁面
    @GetMapping("/auth")
    public String authPage(Model model) {
        return "back-end/auth";
    }

}
