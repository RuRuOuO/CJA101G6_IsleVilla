package com.islevilla.yin.employee.controller;

import com.islevilla.yin.employee.model.Employee;
import com.islevilla.yin.employee.model.EmployeeService;
import com.islevilla.yin.permission.model.Permission;
import com.islevilla.yin.permission.model.PermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@Controller
@RequestMapping("/backend/employee-permission")
public class EmployeePermissionController {

    @Autowired
    private EmployeeService employeeService;
    
    @Autowired
    private PermissionService permissionService;

    /**
     * 顯示員工權限管理頁面
     */
    @GetMapping("/manage/{employeeId}")
    public String manageEmployeePermissions(@PathVariable Integer employeeId, Model model) {
        Employee employee = employeeService.getEmployeeById(employeeId);
        List<Permission> allPermissions = permissionService.getAllPermissions();
        Set<Permission> employeePermissions = employeeService.getEmployeePermissions(employeeId);
        
        model.addAttribute("employee", employee);
        model.addAttribute("allPermissions", allPermissions);
        model.addAttribute("employeePermissions", employeePermissions);
        
        return "back-end/employee/managePermissions";
    }

    /**
     * 新增權限給員工
     */
    @PostMapping("/add-permission")
    @ResponseBody
    public String addPermissionToEmployee(@RequestParam Integer employeeId, 
                                        @RequestParam Integer permissionId) {
        try {
            employeeService.addPermissionToEmployee(employeeId, permissionId);
            return "success";
        } catch (Exception e) {
            return "error: " + e.getMessage();
        }
    }

    /**
     * 移除員工權限
     */
    @PostMapping("/remove-permission")
    @ResponseBody
    public String removePermissionFromEmployee(@RequestParam Integer employeeId, 
                                             @RequestParam Integer permissionId) {
        try {
            employeeService.removePermissionFromEmployee(employeeId, permissionId);
            return "success";
        } catch (Exception e) {
            return "error: " + e.getMessage();
        }
    }
} 