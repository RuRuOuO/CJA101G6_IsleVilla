package com.islevilla.yin.auth;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
import com.islevilla.yin.auth.CustomLoginSuccessHandler;

@Configuration
@EnableMethodSecurity(prePostEnabled = true)  // 啟用 @PreAuthorize 註解
public class SecurityConfig {

    @Autowired
    private CustomLoginSuccessHandler customLoginSuccessHandler;

    // 1) 註冊 PasswordEncoder Bean
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
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

    // 2) SecurityFilterChain
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // 暫時禁用 CSRF 以便測試
                .csrf(csrf -> csrf.disable())

                // 授權規則 (Authorization rules)，注意順序：先公開、再認證、最後 anyRequest
                .authorizeHttpRequests(auth -> auth
                        // —— 一次放行所有公開頁面 ——
                        .requestMatchers(
                                "/backend/auth",           // 登入頁 GET
                                "/css/**", "/js/**", "/images/**", // 靜態資源
                                "/api/**"//放行api測試
                        ).permitAll()

                        // —— 其餘 backend 頁面必須登入 ——
                        .requestMatchers("/backend/**").authenticated()

                        // —— 其餘一律放行 ——
                        .anyRequest().permitAll()
                )

                // 表單登入 Form-Login 設定
                .formLogin(form -> form
                        .loginPage("/backend/auth")           // 自訂登入頁面
                        .loginProcessingUrl("/backend/login/process")  // 處理 POST /login
                        .usernameParameter("email")                     // 表單 field name
                        .passwordParameter("password")
                        .successHandler(customLoginSuccessHandler) // 使用自定義成功處理器
                        .failureHandler(customAuthenticationFailureHandler()) // 使用自定義失敗處理器
                        .permitAll()
                )

                // 登出 Logout 設定
                .logout(logout -> logout
                        .logoutUrl("/backend/logout")
                        .logoutSuccessUrl("/backend/auth?logout")
                        .invalidateHttpSession(true)
                        .permitAll()
                )
        ;

        return http.build();
    }
}
