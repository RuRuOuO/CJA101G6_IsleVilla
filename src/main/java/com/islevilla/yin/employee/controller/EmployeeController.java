package com.islevilla.yin.employee.controller;

import com.islevilla.yin.department.model.DepartmentService;
import com.islevilla.yin.employee.model.Employee;
import com.islevilla.yin.employee.model.EmployeeService;
import com.islevilla.yin.permission.model.Permission;
import com.islevilla.yin.permission.model.PermissionService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.PathVariable;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/backend/employee")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private DepartmentService departmentService;
    @Autowired
    private PermissionService permissionService;
    @Autowired
    private PasswordEncoder passwordEncoder;

//    @GetMapping("/list")
//    public String listEmployee(Model model) {
//
//        return "back-end/employee/listEmployee";
//    }
    //渲染員工列表
    @GetMapping("/list")
    public String listEmployee(@RequestParam(value = "departmentId", required = false) Integer departmentId,
                              @RequestParam(value = "status", required = false) Byte status, 
                              Model model) {
        List<Employee> employees;
        
        if (departmentId != null && status != null) {
            // 根據部門和狀態過濾員工
            employees = employeeService.getEmployeeByDepartmentIdAndStatus(departmentId, status);
        } else if (departmentId != null) {
            // 根據部門過濾員工
            employees = employeeService.getEmployeeByDepartmentId(departmentId);
        } else if (status != null) {
            // 根據狀態過濾員工
            employees = employeeService.getEmployeeByStatus(status);
        } else {
            // 沒有選擇篩選條件時顯示所有員工
            employees = employeeService.getAllEmployees();
        }
        
        model.addAttribute("employees", employees);
        model.addAttribute("departments", departmentService.getAllDepartments());
        model.addAttribute("selectedDepartmentId", departmentId);
        model.addAttribute("selectedStatus", status);
        model.addAttribute("permissions", permissionService.getAllPermissions());
        model.addAttribute("sidebarActive", "employee-list");
        return "back-end/employee/listEmployee";
    }

    // 取得員工資料 API
    @GetMapping("/get/{employeeId}")
    @ResponseBody
    public Employee getEmployee(@PathVariable Integer employeeId) {
        return employeeService.getEmployeeById(employeeId);
    }
    
    // 新增員工 API
    @PostMapping("/add")
    @ResponseBody
    public String addEmployee(@RequestParam String employeeName,
                            @RequestParam String employeeEmail,
                            @RequestParam String employeePassword,
                            @RequestParam Integer departmentId,
                            @RequestParam String employeeMobile,
                            @RequestParam Byte employeeGender,
                            @RequestParam String employeeBirthdate,
                            @RequestParam String employeeHiredate,
                            @RequestParam Byte employeeStatus,
                            @RequestParam String employeeAddress,
                            @RequestParam String permissions,
                            @RequestParam(value = "employeePhoto", required = false) MultipartFile employeePhoto) {
        try {
            // 驗證性別值
            if (employeeGender != 0 && employeeGender != 1) {
                return "error: 性別值必須是 0(女) 或 1(男)";
            }
            // 檢查email是否重複
            if (employeeService.findByEmail(employeeEmail) != null) {
                return "error: Email 已經被使用，請換一個！";
            }
            Employee employee = new Employee();
            employee.setEmployeeName(employeeName);
            employee.setEmployeeEmail(employeeEmail);
            String hashedPassword = passwordEncoder.encode(employeePassword);
            employee.setEmployeePassword(hashedPassword);
            employee.setEmployeeMobile(employeeMobile);
            employee.setEmployeeGender(employeeGender);
            employee.setEmployeeBirthdate(java.time.LocalDate.parse(employeeBirthdate));
            employee.setEmployeeHiredate(java.time.LocalDate.parse(employeeHiredate));
            employee.setEmployeeStatus(employeeStatus);
            employee.setEmployeeAddress(employeeAddress);
            employee.setDepartment(departmentService.getDepartmentById(departmentId));
            ObjectMapper mapper = new ObjectMapper();
            List<String> permissionNames = mapper.readValue(permissions, new TypeReference<List<String>>() {});
            for (String permissionName : permissionNames) {
                List<Permission> permissionsList = permissionService.getPermissionsByName(permissionName);
                if (!permissionsList.isEmpty()) {
                    employee.getPermissions().add(permissionsList.get(0));
                }
            }
            if (employeePhoto != null && !employeePhoto.isEmpty()) {
                employee.setEmployeePhoto(employeePhoto.getBytes());
            }
            employeeService.addEmployee(employee);
            return "success";
        } catch (Exception e) {
            return "error: " + e.getMessage();
        }
    }
    
    // 更新員工資料 API
    @PostMapping("/update")
    @ResponseBody
    public String updateEmployee(@RequestParam Integer employeeId,
                               @RequestParam String employeeName,
                               @RequestParam String employeeEmail,
                               @RequestParam Integer departmentId,
                               @RequestParam Byte employeeGender,
                               @RequestParam Byte employeeStatus,
                               @RequestParam String permissions,
                               @RequestParam(value = "employeePhoto", required = false) MultipartFile employeePhoto) {
        try {
            // 驗證性別值
            if (employeeGender != 0 && employeeGender != 1) {
                return "error: 性別值必須是 0(女) 或 1(男)";
            }
            
            Employee employee = employeeService.getEmployeeById(employeeId);
            if (employee == null) {
                return "員工不存在";
            }
            // 檢查email是否重複（排除自己）
            Employee exist = employeeService.findByEmail(employeeEmail);
            if (exist != null && !exist.getEmployeeId().equals(employeeId)) {
                return "error: Email 已經被其他人使用，請換一個！";
            }
            employee.setEmployeeName(employeeName);
            employee.setEmployeeEmail(employeeEmail);
            employee.setEmployeeGender(employeeGender);
            employee.setEmployeeStatus(employeeStatus);
            employee.setDepartment(departmentService.getDepartmentById(departmentId));
            employee.getPermissions().clear();
            ObjectMapper mapper = new ObjectMapper();
            List<String> permissionNames = mapper.readValue(permissions, new TypeReference<List<String>>() {});
            for (String permissionName : permissionNames) {
                List<Permission> permissionsList = permissionService.getPermissionsByName(permissionName);
                if (!permissionsList.isEmpty()) {
                    employee.getPermissions().add(permissionsList.get(0));
                }
            }
            if (employeePhoto != null && !employeePhoto.isEmpty()) {
                employee.setEmployeePhoto(employeePhoto.getBytes());
            }
            employeeService.updateEmployee(employee);
            
            // 如果更新的是當前登入用戶，重新載入認證資訊
            Authentication currentAuth = SecurityContextHolder.getContext().getAuthentication();
            if (currentAuth != null && currentAuth.getPrincipal() instanceof Employee) {
                Employee currentEmployee = (Employee) currentAuth.getPrincipal();
                if (currentEmployee.getEmployeeId().equals(employeeId)) {
                    // 重新設定權限
                    List<SimpleGrantedAuthority> authorities = employee.getPermissions() != null ?
                        employee.getPermissions().stream()
                            .map(p -> new SimpleGrantedAuthority(p.getPermissionName()))
                            .collect(Collectors.toList()) :
                        List.of(new SimpleGrantedAuthority("USER"));
                    
                    employee.setAuthorities(authorities);
                    
                    // 建立新的認證物件
                    UsernamePasswordAuthenticationToken newAuth = 
                        new UsernamePasswordAuthenticationToken(employee, currentAuth.getCredentials(), authorities);
                    
                    // 更新 SecurityContext
                    SecurityContextHolder.getContext().setAuthentication(newAuth);
                }
            }
            
            return "success";
        } catch (Exception e) {
            return "error: " + e.getMessage();
        }
    }

    // 取得員工圖片
    @GetMapping("/photo/{employeeId}")
    @ResponseBody
    public ResponseEntity<byte[]> getEmployeePhoto(@PathVariable Integer employeeId) {
        Employee emp = employeeService.getEmployeeById(employeeId);
        if (emp != null && emp.getEmployeePhoto() != null) {
            return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, "image/jpeg")
                .body(emp.getEmployeePhoto());
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
