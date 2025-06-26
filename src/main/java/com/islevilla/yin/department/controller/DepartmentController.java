package com.islevilla.yin.department.controller;

import com.islevilla.yin.department.model.Department;
import com.islevilla.yin.department.model.DepartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/backend/department")
public class DepartmentController {

    @Autowired
    private DepartmentService departmentService;

    // 渲染部門列表
    @GetMapping("/list")
    public String listDepartment(Model model) {
        List<Department> departments = departmentService.getAllDepartments();
        model.addAttribute("departments", departments);
        return "back-end/department/listDepartment";
    }

    // 取得部門資料 API
    @GetMapping("/get/{departmentId}")
    @ResponseBody
    public Department getDepartment(@PathVariable Integer departmentId) {
        return departmentService.getDepartmentById(departmentId);
    }
    
    // 新增部門 API
    @PostMapping("/add")
    @ResponseBody
    public String addDepartment(@RequestParam String departmentName) {
        try {
            Department department = new Department();
            department.setDepartmentName(departmentName);
            departmentService.addDepartment(department);
            return "success";
        } catch (Exception e) {
            return "error: " + e.getMessage();
        }
    }
    
    // 更新部門資料 API
    @PostMapping("/update")
    @ResponseBody
    public String updateDepartment(@RequestParam Integer departmentId,
                                 @RequestParam String departmentName) {
        try {
            Department department = departmentService.getDepartmentById(departmentId);
            if (department == null) {
                return "部門不存在";
            }
            department.setDepartmentName(departmentName);
            departmentService.updateDepartment(department);
            return "success";
        } catch (Exception e) {
            return "error: " + e.getMessage();
        }
    }

    // 刪除部門 API
    @PostMapping("/delete")
    @ResponseBody
    public String deleteDepartment(@RequestParam Integer departmentId) {
        try {
            departmentService.deleteDepartment(departmentId);
            return "success";
        } catch (Exception e) {
            return "error: " + e.getMessage();
        }
    }
} 