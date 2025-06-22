package com.islevilla.yin.registration;

import com.islevilla.yin.department.model.DepartmentService;
import com.islevilla.yin.employee.model.Employee;
import com.islevilla.yin.department.model.Department;

import com.islevilla.yin.employee.model.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/backend/employee")
public class RegistrationController {

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private DepartmentService departmentService;


    // 顯示註冊表單
    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("employee", new Employee());
        model.addAttribute("department", departmentService.getAllDepartments());
        return "back-end/employee/register";
    }

    // 處理註冊提交
    @PostMapping("/register_process")
    public String processRegister(@ModelAttribute Employee employee) {
        // 1) 密碼加密
        String rawPwd = employee.getEmployeePassword();
        String hashPwd = passwordEncoder.encode(rawPwd);
        employee.setEmployeePassword(hashPwd);

        // 2) 設定預設值
        employee.setEmployeeStatus((byte) 1); // 預設為在職
        employee.setEmployeeHiredate(java.time.LocalDate.now()); // 入職日期預設為註冊當天
//        Department dept = new Department();
//        dept.setDepartmentId(1); // 設定部門 id
//        employee.setDepartment(dept); // 設定到 employee

        // 3) 存到資料庫
        employeeService.addEmployee(employee);

        // 4) 註冊完成後導回登入
        return "redirect:/backend/employee/login";
    }
}
