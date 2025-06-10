package com.islevilla.yin.permission.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Entity
@Table(name = "permission")
@Data
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
}
