package com.islevilla.yin.employee.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee, Integer> {
    List<Employee> findByDepartmentDepartmentId(Integer departmentId);
    Employee findByEmployeeEmail(String email);

    // 新增：查詢員工時一併載入權限
    @Query("SELECT e FROM Employee e LEFT JOIN FETCH e.permissions WHERE e.employeeEmail = :email")
    Optional<Employee> findByEmployeeEmailWithPermissions(@Param("email") String email);
    
    // 新增：查詢所有員工時一併載入權限
    @Query("SELECT DISTINCT e FROM Employee e LEFT JOIN FETCH e.permissions")
    List<Employee> findAllWithPermissions();
    
    // 新增：根據部門查詢員工時一併載入權限
    @Query("SELECT DISTINCT e FROM Employee e LEFT JOIN FETCH e.permissions WHERE e.department.departmentId = :departmentId")
    List<Employee> findByDepartmentDepartmentIdWithPermissions(@Param("departmentId") Integer departmentId);
    
    // 新增：根據ID查詢員工時一併載入權限
    @Query("SELECT e FROM Employee e LEFT JOIN FETCH e.permissions WHERE e.employeeId = :employeeId")
    Optional<Employee> findByIdWithPermissions(@Param("employeeId") Integer employeeId);
    
    // 新增：根據狀態查詢員工時一併載入權限
    @Query("SELECT DISTINCT e FROM Employee e LEFT JOIN FETCH e.permissions WHERE e.employeeStatus = :status")
    List<Employee> findByEmployeeStatusWithPermissions(@Param("status") Byte status);
    
    // 新增：根據部門和狀態查詢員工時一併載入權限
    @Query("SELECT DISTINCT e FROM Employee e LEFT JOIN FETCH e.permissions WHERE e.department.departmentId = :departmentId AND e.employeeStatus = :status")
    List<Employee> findByDepartmentDepartmentIdAndEmployeeStatusWithPermissions(@Param("departmentId") Integer departmentId, @Param("status") Byte status);
}
