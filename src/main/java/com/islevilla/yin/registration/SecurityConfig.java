package com.islevilla.yin.registration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    // 1) 註冊 PasswordEncoder Bean
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // 2) SecurityFilterChain
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // 授權規則 (Authorization rules)，注意順序：先公開、再認證、最後 anyRequest
                .authorizeHttpRequests(auth -> auth
                        // —— 一次放行所有公開頁面 ——
                        .requestMatchers(
                                "/backend/employee/login",           // 登入頁 GET
                                "/backend/employee/register",        // 註冊頁 GET
                                "/backend/employee/register_process",// 註冊表單 POST
                                "/css/**", "/js/**", "/images/**"    // 靜態資源
                        ).permitAll()

                        // —— backend 其他頁面必須登入 ——
                        .requestMatchers("/backend/**").authenticated()

                        // —— 其餘一律放行 ——
                        .anyRequest().permitAll()
                )

                // 表單登入 Form-Login 設定
                .formLogin(form -> form
                        .loginPage("/backend/employee/login")           // 自訂登入頁面
                        .loginProcessingUrl("/backend/employee/login")  // 處理 POST /login
                        .usernameParameter("email")                     // 表單 field name
                        .passwordParameter("password")
                        .defaultSuccessUrl("/backend/employee/list", true) // 登入成功跳轉
                        .permitAll()
                )

                // 登出 Logout 設定
                .logout(logout -> logout
                        .logoutUrl("/backend/employee/logout")
                        .logoutSuccessUrl("/backend/employee/login?logout")
                        .invalidateHttpSession(true)
                        .permitAll()
                )
        ;

        return http.build();
    }
}
