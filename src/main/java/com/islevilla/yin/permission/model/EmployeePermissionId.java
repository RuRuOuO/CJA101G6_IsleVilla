package com.islevilla.yin.permission.model;

import java.io.Serializable;
import java.util.Objects;

public class EmployeePermissionId implements Serializable {
    private Integer employeeId;
    private Integer permissionId;

    public EmployeePermissionId() {}

    public EmployeePermissionId(Integer employeeId, Integer permissionId) {
        this.employeeId = employeeId;
        this.permissionId = permissionId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EmployeePermissionId)) return false;
        EmployeePermissionId that = (EmployeePermissionId) o;
        return Objects.equals(employeeId, that.employeeId) &&
               Objects.equals(permissionId, that.permissionId);
    }
    @Override
    public int hashCode() {
        return Objects.hash(employeeId, permissionId);
    }
} 