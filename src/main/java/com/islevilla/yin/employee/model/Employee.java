package com.islevilla.yin.employee.model;

import com.islevilla.yin.department.model.Department;
import com.islevilla.yin.permission.model.Permission;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "employee")
@Data
@EqualsAndHashCode(exclude = "permissions")
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "employee_id")
    private Integer employeeId;

    @ManyToOne
    @JoinColumn(name = "department_id", referencedColumnName = "department_id", nullable = false)
    @NotNull(message = "部門必填")
    private Department department;

    @NotBlank(message = "姓名必填")
    @Column(name = "employee_name")
    private String employeeName;

    @NotBlank(message = "信箱必填")
    @Email(message = "信箱格式錯誤")
    @Column(name = "employee_email")
    private String employeeEmail;

    @NotBlank(message = "地址必填")
    @Column(name = "employee_address")
    private String employeeAddress;

    @NotBlank(message = "手機必填")
    @Pattern(regexp = "^09\\d{8}$", message = "手機格式錯誤")
    @Column(name = "employee_mobile")
    private String employeeMobile;


    @Column(name = "employee_password_hash")
    private String employeePassword;

    @NotNull(message = "性別必填")
    @Column(name = "employee_gender")
    private Byte employeeGender; // 0:女, 1:男

    @NotNull(message = "生日必填")
    @Column(name = "employee_birthdate")
    private LocalDate employeeBirthdate;

//    @Column(name = "employee_photo")
//    private byte[] employeePhoto; // 使用 byte[] 存儲圖片

    @Column(name = "employee_hiredate")
    private LocalDate employeeHiredate;

    @Column(name = "employee_leavedate")
    private LocalDate employeeLeavedate;

    @Column(name = "employee_status")
    private Byte employeeStatus; // 0:離職, 1:在職, 2:停職

    @ManyToMany
    @JoinTable(
        name = "employee_permission",
        joinColumns = @JoinColumn(name = "employee_id"),
        inverseJoinColumns = @JoinColumn(name = "permission_id")
    )
    @ToString.Exclude
    private Set<Permission> permissions = new HashSet<>();

}
