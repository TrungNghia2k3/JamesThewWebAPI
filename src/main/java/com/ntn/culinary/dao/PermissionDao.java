package com.ntn.culinary.dao;

import com.ntn.culinary.model.Permission;

import java.util.List;

public interface PermissionDao {
    boolean existsByName(String name);

    void insertPermission(Permission permission);

    void updatePermission(Permission permission);

    void deletePermissionById(int id);

    List<Permission> getAllPermissions();

    Permission getPermissionById(int id);

    boolean existsById(int id);

    Permission getPermissionByName(String name);
}
