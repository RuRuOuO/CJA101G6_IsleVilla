package com.islevilla.yin.permission.model;

import com.islevilla.yin.employee.model.Employee;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "employee_permission")
@IdClass(EmployeePermissionId.class)
@Data
public class EmployeePermission {
    @Id
    @Column(name = "employee_id")
    private Integer employeeId;

    @Id
    @Column(name = "permission_id")
    private Integer permissionId;

    @ManyToOne
    @JoinColumn(name = "employee_id", insertable = false, updatable = false)
    private Employee employee;

    @ManyToOne
    @JoinColumn(name = "permission_id", insertable = false, updatable = false)
    private Permission permission;
}
