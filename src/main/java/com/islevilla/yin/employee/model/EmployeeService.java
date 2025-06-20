package com.islevilla.yin.employee.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class EmployeeService {
    @Autowired
    private EmployeeRepository employeeRepository;

    public void addEmployee(Employee employee) {
        employeeRepository.save(employee);
    }
    public void updateEmployee(Employee employee) {
        employeeRepository.save(employee);
    }
    public void deleteEmployee(Integer employeeId) {
        if(employeeRepository.existsById(employeeId)){
            employeeRepository.deleteById(employeeId);
        }
    }
    public Employee getEmployeeById(Integer employeeId) {
        return employeeRepository.findById(employeeId)
                .orElse(null);
    }
    public List<Employee> getEmployeeByDepartmentId(Integer departmentId) {
        return employeeRepository.findByDepartmentDepartmentId(departmentId);
    }
    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }
}
