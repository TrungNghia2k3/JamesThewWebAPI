package com.ntn.culinary.service;

import com.ntn.culinary.request.PermissionRequest;
import com.ntn.culinary.response.PermissionResponse;

import java.util.List;

public interface PermissionService {
    void addPermission(PermissionRequest permissionRequest);

    void updatePermission(PermissionRequest permissionRequest);

    PermissionResponse getPermissionById(int id);

    void deletePermissionById(int id);

    List<PermissionResponse> getAllPermissions();

    PermissionResponse getPermissionByName(String name);
}
