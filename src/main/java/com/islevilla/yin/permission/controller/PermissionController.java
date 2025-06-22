package com.islevilla.yin.permission.controller;

import com.islevilla.yin.permission.model.Permission;
import com.islevilla.yin.permission.model.PermissionService;
import com.islevilla.yin.product.model.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class PermissionController {

    @Autowired
    private PermissionService permissionService;

    //後台-權限列表
    @GetMapping("/backend/permission/list")
    public String listPermission(Model model) {
        List<Permission> permissionList = permissionService.getAllPermissions();
        model.addAttribute("permissionList", permissionList); // 權限列表
        model.addAttribute("permission", new Permission());   // 空的 Permission 給 modal 表單用
        return "back-end/employee/permission";
    }
}
