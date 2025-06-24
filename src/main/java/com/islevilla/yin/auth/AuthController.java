package com.islevilla.yin.auth;

import com.islevilla.yin.employee.model.Employee;
import com.islevilla.yin.employee.model.EmployeeService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/backend")
public class AuthController {

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // 登入頁面
    @GetMapping("/auth")
    public String authPage(Model model) {
        return "back-end/auth";
    }

    // 登入頁面（重定向）
    @GetMapping("/login")
    public String loginPage(Model model) {
        return "redirect:/backend/auth";
    }

    // 功能-登入處理
    @PostMapping("/login/process")
    public String login(@RequestParam String email,
                        @RequestParam String password,
                        HttpSession session,
                        Model model) {
        // 使用findByEmailWithPermissions方法，與MyUserDetailsService保持一致
        Employee employee = employeeService.findByEmailWithPermissions(email);

        if (employee == null) {
            model.addAttribute("error", "帳號或密碼錯誤");
            return "back-end/auth";
        }

        // 檢查員工狀態，離職(0)或停職(2)的員工無法登入
        if (employee.getEmployeeStatus() == 0) {
            model.addAttribute("error", "此帳號已離職，無法登入");
            return "back-end/auth";
        }
        if (employee.getEmployeeStatus() == 2) {
            model.addAttribute("error", "此帳號已停職，無法登入");
            return "back-end/auth";
        }

        // 暫時移除權限檢查，讓員工能夠登入
        // if (employee.getPermissions() == null || employee.getPermissions().isEmpty()) {
        //     model.addAttribute("error", "此帳號沒有權限，無法登入");
        //     return "back-end/auth";
        // }

        if (passwordEncoder.matches(password, employee.getEmployeePassword())) {
            session.setAttribute("currentUser", employee);
            return "redirect:/backend/dashboard";
        } else {
            model.addAttribute("error", "帳號或密碼錯誤");
            return "back-end/auth";
        }
    }

    // 功能-登出處理
    @GetMapping("/logout/process")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/backend/auth";
    }

    // 權限測試頁面
    @GetMapping("/test-permissions")
    public String testPermissions() {
        return "back-end/test-permissions";
    }
}
