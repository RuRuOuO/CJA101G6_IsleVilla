package com.islevilla.yin.registration;

import com.islevilla.yin.employee.model.Employee;
import com.islevilla.yin.employee.model.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.security.core.userdetails.UserDetails;


@Service
public class MyUserDetailsService implements UserDetailsService {
    @Autowired
    private EmployeeService empSvc;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Employee e = empSvc.findByEmail(email);
        if (e == null) {
            throw new UsernameNotFoundException("找不到使用者：" + email);
        }
        // 用 Spring Security 的 User.builder()
        return User.builder()
                .username(e.getEmployeeEmail())
                .password(e.getEmployeePassword())  // 注意：事先在 DB 裡用 BCrypt 加密
                .roles("EMPLOYEE")                  // 或 ROLE_USER 等
                .build();
    }
}