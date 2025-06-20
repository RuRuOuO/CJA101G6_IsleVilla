package com.islevilla.yin.employee.controller;

import com.islevilla.yin.employee.model.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/backend/employee")
public class EmployeeController {

        @Autowired
        private EmployeeService employeeService;

        @GetMapping("/list")
        public String listEmployee(Model model) {
            // 獲取所有員工資料
            model.addAttribute("employee", employeeService.getAllEmployees());
            // 返回員工列表頁面
            return "back-end/employee/listEmployee";
        }

}
