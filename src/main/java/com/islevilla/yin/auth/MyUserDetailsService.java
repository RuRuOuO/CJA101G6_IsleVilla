package com.islevilla.yin.auth;

import com.islevilla.yin.employee.model.Employee;
import com.islevilla.yin.employee.model.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;
import java.util.stream.Collectors;


@Service
public class MyUserDetailsService implements UserDetailsService {
    @Autowired
    private EmployeeService employeeService;
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Employee e = employeeService.findByEmailWithPermissions(email);
        if (e == null) {
            throw new UsernameNotFoundException("找不到使用者：" + email);
        }

        // 檢查員工狀態，離職(0)或停職(2)的員工無法登入
        if (e.getEmployeeStatus() == 0) {
            throw new UsernameNotFoundException("此帳號已離職，無法登入");
        }
        if (e.getEmployeeStatus() == 2) {
            throw new UsernameNotFoundException("此帳號已停職，無法登入");
        }

        // 暫時移除權限檢查，讓員工能夠登入
        // if (e.getPermissions() == null || e.getPermissions().isEmpty()) {
        //     throw new UsernameNotFoundException("此帳號沒有權限，無法登入");
        // }

        // 權限名稱直接對應 permission_name 欄位
        List<SimpleGrantedAuthority> authorities = e.getPermissions() != null ?
            e.getPermissions().stream()
                .map(p -> new SimpleGrantedAuthority(p.getPermissionName()))
                .collect(Collectors.toList()) :
            List.of(new SimpleGrantedAuthority("USER")); // 預設權限

        // 設定權限到Employee物件
        e.setAuthorities(authorities);
        
        return e;
    }
}