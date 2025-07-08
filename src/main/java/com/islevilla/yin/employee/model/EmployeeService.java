package com.islevilla.yin.employee.model;

import com.islevilla.yin.permission.model.Permission;
import com.islevilla.yin.permission.model.PermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Component
public class EmployeeService {
    @Autowired
    private EmployeeRepository employeeRepository;
    
    @Autowired
    private PermissionService permissionService;

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
        return employeeRepository.findByIdWithPermissions(employeeId)
                .orElse(null);
    }
    public List<Employee> getEmployeeByDepartmentId(Integer departmentId) {
        return employeeRepository.findByDepartmentDepartmentIdWithPermissions(departmentId);
    }
    public List<Employee> getEmployeeByStatus(Byte status) {
        return employeeRepository.findByEmployeeStatusWithPermissions(status);
    }
    public List<Employee> getEmployeeByDepartmentIdAndStatus(Integer departmentId, Byte status) {
        return employeeRepository.findByDepartmentDepartmentIdAndEmployeeStatusWithPermissions(departmentId, status);
    }
    public List<Employee> getAllEmployees() {
        return employeeRepository.findAllWithPermissions();
    }
    public Employee findByEmail(String email) {
        return employeeRepository.findByEmployeeEmail(email);
    }

    // 新增：查詢員工時一併載入權限
    public Employee findByEmailWithPermissions(String email) {
        return employeeRepository.findByEmployeeEmailWithPermissions(email).orElse(null);
    }

    /**
     * 根據部門自動分配預設權限
     */
    @Transactional
    public void assignDefaultPermissionsByDepartment(Employee employee) {
        // 根據部門ID分配對應的權限
        Integer departmentId = employee.getDepartment().getDepartmentId();
        
        // 部門ID對應權限名稱的映射
        String permissionName = getPermissionNameByDepartment(departmentId);
        
        if (permissionName != null) {
            // 查找對應的權限
            List<Permission> permissions = permissionService.getPermissionsByName(permissionName);
            if (!permissions.isEmpty()) {
                // 將權限加入員工的權限集合
                employee.getPermissions().addAll(permissions);
            }
        }
    }
    
    /**
     * 根據部門ID獲取對應的權限名稱
     */
    private String getPermissionNameByDepartment(Integer departmentId) {
        switch (departmentId) {
            case 1: return "operation"; // 營運部
            case 2: return "service";   // 客服部
            case 3: return "shuttle";   // 接駁部
            case 4: return "room";      // 房務部
            case 5: return "product";   // 產品部
            default: return null;
        }
    }
    
    /**
     * 為員工新增權限
     */
    @Transactional
    public void addPermissionToEmployee(Integer employeeId, Integer permissionId) {
        Employee employee = getEmployeeById(employeeId);
        Permission permission = permissionService.getPermissionById(permissionId);
        
        if (employee != null && permission != null) {
            employee.getPermissions().add(permission);
            employeeRepository.save(employee);
        }
    }
    
    /**
     * 移除員工權限
     */
    @Transactional
    public void removePermissionFromEmployee(Integer employeeId, Integer permissionId) {
        Employee employee = getEmployeeById(employeeId);
        Permission permission = permissionService.getPermissionById(permissionId);
        
        if (employee != null && permission != null) {
            employee.getPermissions().remove(permission);
            employeeRepository.save(employee);
        }
    }
    
    /**
     * 獲取員工的所有權限
     */
    public Set<Permission> getEmployeePermissions(Integer employeeId) {
        Employee employee = getEmployeeById(employeeId);
        return employee != null ? employee.getPermissions() : null;
    }
    
    /**
     * 檢查員工是否有特定權限
     */
    public boolean hasPermission(Integer employeeId, String permissionName) {
        Employee employee = getEmployeeById(employeeId);
        if (employee != null) {
            return employee.getPermissions().stream()
                    .anyMatch(p -> p.getPermissionName().equals(permissionName));
        }
        return false;
    }

    public List<Employee> searchEmployees(Integer departmentId, Byte status, String keyword) {
        if (departmentId != null && status != null) {
            return employeeRepository.findByDepartment_DepartmentIdAndEmployeeStatusAndEmployeeNameContainingIgnoreCase(departmentId, status, keyword);
        } else if (departmentId != null) {
            return employeeRepository.findByDepartment_DepartmentIdAndEmployeeNameContainingIgnoreCase(departmentId, keyword);
        } else if (status != null) {
            return employeeRepository.findByEmployeeStatusAndEmployeeNameContainingIgnoreCase(status, keyword);
        } else {
            return employeeRepository.findByEmployeeNameContainingIgnoreCase(keyword);
        }
    }
}
