package com.islevilla.yin.auth;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.core.AuthenticationException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import com.islevilla.jay.operationLog.controller.CustomLoginSuccessHandler;
import com.islevilla.jay.operationLog.controller.CustomLogoutSuccessHandler;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;

@Configuration
@EnableMethodSecurity(prePostEnabled = true)  // 啟用 @PreAuthorize 註解
public class SecurityConfig {

    @Autowired
    private CustomLoginSuccessHandler customLoginSuccessHandler;
    
    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private CustomLogoutSuccessHandler customLogoutSuccessHandler;

    // 1) 註冊 PasswordEncoder Bean
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    // 2) 註冊 AuthenticationManager Bean
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    // 自定義認證失敗處理器
    @Bean
    public AuthenticationFailureHandler customAuthenticationFailureHandler() {
        return new AuthenticationFailureHandler() {
            @Override
            public void onAuthenticationFailure(HttpServletRequest request,
                                              HttpServletResponse response,
                                              AuthenticationException exception)
                    throws IOException, ServletException {

                String errorMessage = "帳號或密碼錯誤";

                // 根據異常類型設定不同的錯誤訊息
                if (exception.getMessage() != null) {
                    if (exception.getMessage().contains("離職")) {
                        errorMessage = "此帳號已離職，無法登入";
                    } else if (exception.getMessage().contains("停職")) {
                        errorMessage = "此帳號已停職，無法登入";
                    } else if (exception.getMessage().contains("找不到使用者")) {
                        errorMessage = "帳號或密碼錯誤";
                    }
                }

                // 重定向到登入頁面並帶上錯誤訊息
                response.sendRedirect("/backend/auth?error=" + java.net.URLEncoder.encode(errorMessage, "UTF-8"));
            }
        };
    }

    // 3) 員工後台系統的 SecurityFilterChain
    @Bean
    @Order(1)
    public SecurityFilterChain employeeFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/backend/**")  // 只處理 /backend/** 路徑
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/backend/auth").permitAll()  // 員工登入頁面
                        .anyRequest().authenticated()  // 其他 backend 路徑需要認證
                )
                
                .formLogin(form -> form
                        .loginPage("/backend/auth")
                        .loginProcessingUrl("/backend/login/process")
                        .usernameParameter("email")
                        .passwordParameter("password")
                        .successHandler(customLoginSuccessHandler)
                        .failureHandler(customAuthenticationFailureHandler())
                        .permitAll()
                )
                
                .logout(logout -> logout
                        .logoutUrl("/backend/logout")
                        .logoutSuccessHandler(customLogoutSuccessHandler) // 註冊自訂登出 Handler
                        .logoutSuccessUrl("/backend/auth?logout")
                        .invalidateHttpSession(true)
                        .permitAll()
                )
                
                .userDetailsService(userDetailsService);
        
        return http.build();
    }

}
