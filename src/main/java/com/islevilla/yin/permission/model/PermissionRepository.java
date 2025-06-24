package com.islevilla.yin.permission.model;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PermissionRepository extends JpaRepository<Permission, Integer> {
    List<Permission> findByPermissionName(String permissionName);
}
