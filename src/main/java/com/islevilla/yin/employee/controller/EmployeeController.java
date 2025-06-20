package com.islevilla.yin.employee.controller;

import com.islevilla.yin.department.model.DepartmentService;
import com.islevilla.yin.employee.model.Employee;
import com.islevilla.yin.employee.model.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/backend/employee")
public class EmployeeController {

        @Autowired
        private EmployeeService employeeService;
        @Autowired
        private DepartmentService departmentService;

//    @GetMapping("/list")
//    public String listEmployee(Model model) {
//
//        return "back-end/employee/listEmployee";
//    }
        //渲染員工列表
        @GetMapping("/list")
        public String listEmployee(@RequestParam(value = "departmentId", required = false) Integer departmentId, Model model) {
            List<Employee> employee;
            if (departmentId != null) {
                // 根據部門過濾員工
                employee = employeeService.getEmployeeByDepartmentId(departmentId);
            } else {
                // 沒有選擇部門時顯示所有員工
                employee = employeeService.getAllEmployees();
            }
            model.addAttribute("employee", employee);
            model.addAttribute("departments", departmentService.getAllDepartments());
            model.addAttribute("selectedDepartmentId", departmentId); // 新增這行
            // 返回員工列表頁面
            return "back-end/employee/listEmployee";
        }



}
