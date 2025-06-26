//package com.islevilla.lai.auth;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.web.SecurityFilterChain;
//
//@Configuration
//@EnableWebSecurity
//@EnableMethodSecurity
//public class MembersSecurityConfig {
//	
//	@Bean
//    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//        http
//            .authorizeHttpRequests(auth -> auth
//                // 不需要登入的路徑（靜態資源、首頁、登入註冊頁等）
//                .requestMatchers(
//                		"/",                       // 首頁
//                		"/member/login",           // 會員登入
//                		"/member/register",        // 會員註冊
//                		"/member/forgot-password", // 會員忘記密碼
//                		"/css/**",                 // 靜態資源
//                		"/js/**",                  // 靜態資源
//                		"/images/**")              // 靜態資源
//                .permitAll()
//                
//                // 需要登入的路徑（例如會員中心）
//                .requestMatchers(
//                		"/member/**",  // 會員其他頁面
//                		"/shuttle/**") // 接駁預約相關頁面
//                .authenticated()
//
//                // 其他預設全部都要登入
////                .anyRequest().authenticated()
//            )
//            .formLogin(form -> form
//                .loginPage("/member/login")             // 顯示登入頁面
//                .loginProcessingUrl("/member/login")    // 處理登入表單提交的 URL（要對應 form）
//                .usernameParameter("memberEmail")
//                .passwordParameter("memberPassword")
//                .defaultSuccessUrl("/", true)           // 登入成功導向
//                .failureUrl("/member/login?error=true") // 登入失敗導向
//                .permitAll()
//            )
//            .logout(logout -> logout
//                .logoutUrl("/member/logout")
//                .logoutSuccessUrl("/member/logout-success")
//                .permitAll()
//            );
//
//        return http.build();
//    }
//
//}