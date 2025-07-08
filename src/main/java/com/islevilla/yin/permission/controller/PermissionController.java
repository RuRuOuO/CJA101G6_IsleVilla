package com.islevilla.yin.permission.controller;

import com.islevilla.yin.permission.model.Permission;
import com.islevilla.yin.permission.model.PermissionService;
import com.islevilla.yin.product.model.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class PermissionController {

    @Autowired
    private PermissionService permissionService;

    
    // 取得權限資料 API
    @GetMapping("/backend/permission/get/{permissionId}")
    @ResponseBody
    public Permission getPermission(@PathVariable Integer permissionId) {
        return permissionService.getPermissionById(permissionId);
    }
    
    // 新增權限 API
    @PostMapping("/backend/permission/add")
    @ResponseBody
    public String addPermission(@RequestParam String permissionName,
                              @RequestParam String permissionDescription) {
        try {
            Permission permission = new Permission();
            permission.setPermissionName(permissionName);
            permission.setPermissionDescription(permissionDescription);
            
            permissionService.addPermission(permission);
            return "success";
        } catch (Exception e) {
            return "error: " + e.getMessage();
        }
    }
    
    // 更新權限 API
    @PostMapping("/backend/permission/update")
    @ResponseBody
    public String updatePermission(@RequestParam Integer permissionId,
                                 @RequestParam String permissionName,
                                 @RequestParam String permissionDescription) {
        try {
            Permission permission = permissionService.getPermissionById(permissionId);
            if (permission == null) {
                return "權限不存在";
            }
            
            permission.setPermissionName(permissionName);
            permission.setPermissionDescription(permissionDescription);
            
            permissionService.updatePermission(permission);
            return "success";
        } catch (Exception e) {
            return "error: " + e.getMessage();
        }
    }
    
    // 刪除權限 API
    @DeleteMapping("/backend/permission/delete/{permissionId}")
    @ResponseBody
    public String deletePermission(@PathVariable Integer permissionId) {
        try {
            permissionService.deletePermission(permissionId);
            return "success";
        } catch (Exception e) {
            return "error: " + e.getMessage();
        }
    }
}
