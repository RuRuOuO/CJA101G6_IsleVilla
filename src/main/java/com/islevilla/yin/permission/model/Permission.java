package com.islevilla.yin.permission.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import com.islevilla.yin.employee.model.Employee;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "permission")
@Data
@EqualsAndHashCode(exclude = "employees")
public class Permission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "permission_id")
    private Integer permissionId;

    @Column(name = "permission_name")
    @NotEmpty(message = "請輸入權限名稱")
    private String permissionName;

    @Column(name = "permission_description")
    @NotEmpty(message = "請輸入權限描述")
    private String permissionDescription;

    @ManyToMany(mappedBy = "permissions")
    @ToString.Exclude
    @JsonIgnore
    private Set<Employee> employees = new HashSet<>();
}
