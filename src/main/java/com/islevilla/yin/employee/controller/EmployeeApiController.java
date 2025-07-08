package com.islevilla.yin.employee.controller;

import com.islevilla.yin.employee.model.Employee;
import com.islevilla.yin.employee.model.EmployeeService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
@PreAuthorize("hasAuthority('operation')")
public class EmployeeApiController {
    @Autowired
    private EmployeeService employeeService;

    // 送出新增員工
    @PostMapping("/add")
    public void addEmployee(@RequestBody Employee employee) {
        employeeService.addEmployee(employee);
    }
    // 送出修改員工
    @PostMapping("/edit")
    public void updateEmployee(@Valid @RequestBody Employee employee) {
        employeeService.updateEmployee(employee);
    }
}
