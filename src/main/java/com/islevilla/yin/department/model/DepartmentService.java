package com.islevilla.yin.department.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import com.islevilla.yin.employee.model.EmployeeService;

@Component
public class DepartmentService {
    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private EmployeeService employeeService;

    public List<Department> getAllDepartments() {
        return departmentRepository.findAll();
    }
    
    public Department getDepartmentById(Integer departmentId) {
        return departmentRepository.findById(departmentId).orElse(null);
    }

    public Department addDepartment(Department department) {
        return departmentRepository.save(department);
    }

    public Department updateDepartment(Department department) {
        return departmentRepository.save(department);
    }

    public void deleteDepartment(Integer departmentId) {
        if (employeeService.getEmployeeByDepartmentId(departmentId).size() > 0) {
            throw new RuntimeException("部門內還有員工，不能刪除該部門");
        }
        if (departmentRepository.existsById(departmentId)) {
            departmentRepository.deleteById(departmentId);
        }
    }
}
