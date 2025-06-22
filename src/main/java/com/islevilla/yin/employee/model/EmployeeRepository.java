package com.islevilla.yin.employee.model;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EmployeeRepository extends JpaRepository<Employee, Integer> {
    List<Employee> findByDepartmentDepartmentId(Integer departmentId);
    Employee findByEmployeeEmail(String email);

}
