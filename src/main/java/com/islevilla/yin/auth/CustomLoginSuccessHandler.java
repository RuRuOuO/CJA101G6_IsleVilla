package com.islevilla.yin.auth;

import com.islevilla.yin.employee.model.Employee;
import com.islevilla.yin.employee.model.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@Component
public class CustomLoginSuccessHandler implements AuthenticationSuccessHandler {

    @Autowired
    private EmployeeService employeeService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        // 取得登入的 email
        String email = authentication.getName();
        Employee employee = employeeService.findByEmailWithPermissions(email);
        HttpSession session = request.getSession();
        session.setAttribute("employee", employee); // 關鍵：放進 session
        System.out.println("[LoginSuccessHandler] 已將 employee 放入 session: " + employee);
        response.sendRedirect("/backend/dashboard");
    }
} 