package com.islevilla.yin.employee.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Table(name = "employee")
@Data
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "employee_id")
    private Integer employeeId;

    @Column(name = "department_id")
    private Integer departmentId;

    @Column(name = "employee_name")
    private String employeeName;

    @Column(name = "employee_email")
    private String employeeEmail;

    @Column(name = "employee_address")
    private String employeeAddress;

    @Column(name = "employee_mobile")
    private String employeeMobile;

    @Column(name = "employee_gender")
    private byte employeeGender; // 0:女, 1:男

    @Column(name = "employee_birthdate")
    private LocalDate employeeBirthdate;

//    @Column(name = "employee_photo")
//    private

    @Column(name = "employee_hiredate")
    private LocalDate employeeHiredate;

    @Column(name = "employee_leavedate")
    private LocalDate employeeLeavedate;

    @Column(name = "employee_status")
    private byte employeeStatus; // 0:離職, 1:在職

}
