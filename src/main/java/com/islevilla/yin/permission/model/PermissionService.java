package com.islevilla.yin.permission.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PermissionService {
    @Autowired
    private PermissionRepository permissionRepository;

    public Permission addPermission(Permission permission) {
        return permissionRepository.save(permission);
    }
    public Permission updatePermission(Permission permission) {
        return permissionRepository.save(permission);
    }
    public void deletePermission(Integer permissionId) {
        permissionRepository.deleteById(permissionId);
    }
    public Permission getPermissionById(Integer permissionId) {
        return permissionRepository.findById(permissionId).orElse(null);
    }
    public List<Permission> getAllPermissions() {
        return permissionRepository.findAll();
    }
    public List<Permission> getPermissionsByName(String permissionName) {
        return permissionRepository.findByPermissionName(permissionName);
    }
}
