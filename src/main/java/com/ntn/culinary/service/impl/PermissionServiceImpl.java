package com.ntn.culinary.service.impl;

import com.ntn.culinary.dao.PermissionDao;
import com.ntn.culinary.dao.StaffPermissionsDao;
import com.ntn.culinary.exception.ConflictException;
import com.ntn.culinary.model.Permission;
import com.ntn.culinary.request.PermissionRequest;
import com.ntn.culinary.response.PermissionResponse;
import com.ntn.culinary.service.PermissionService;

import java.util.List;

public class PermissionServiceImpl implements PermissionService {
    private final StaffPermissionsDao staffPermissionsDao;
    private final PermissionDao permissionDao;

    public PermissionServiceImpl(StaffPermissionsDao staffPermissionsDao, PermissionDao permissionDao) {
        this.staffPermissionsDao = staffPermissionsDao;
        this.permissionDao = permissionDao;
    }

    @Override
    public void addPermission(PermissionRequest permissionRequest) {
        if (!permissionDao.existsByName(permissionRequest.getName())) {
            permissionDao.insertPermission(mapPermissionRequestToPermission(permissionRequest));
        } else {
            throw new ConflictException("Permission with name '" + permissionRequest.getName() + "' already exists.");
        }
    }

    @Override
    public void updatePermission(PermissionRequest permissionRequest) {
        if (permissionDao.existsById(permissionRequest.getId())) {
            if (!permissionDao.getPermissionById(permissionRequest.getId()).getName().equalsIgnoreCase(permissionRequest.getName())
                && permissionDao.existsByName(permissionRequest.getName())) {
                throw new ConflictException("Permission with name '" + permissionRequest.getName() + "' already exists.");
            }
            permissionDao.updatePermission(permissionDao.getPermissionById(permissionRequest.getId()));
        } else {
            throw new ConflictException("Permission with ID " + permissionRequest.getId() + " does not exist.");
        }
    }

    @Override
    public PermissionResponse getPermissionById(int id) {
        if (permissionDao.existsById(id)) {
            return mapPermissionToResponse(permissionDao.getPermissionById(id));
        } else {
            throw new ConflictException("Permission with ID " + id + " does not exist.");
        }
    }

    @Override
    public void deletePermissionById(int id) {
        if (staffPermissionsDao.existsPermissionId(id)) {
            throw new ConflictException("Permission with ID cannot be deleted because it is assigned to roles.");
        }

        if (permissionDao.existsById(id)) {
            permissionDao.deletePermissionById(id);
        } else {
            throw new ConflictException("Permission with ID " + id + " does not exist.");
        }
    }

    @Override
    public List<PermissionResponse> getAllPermissions() {
        List<Permission> permissions = permissionDao.getAllPermissions();
        if (permissions.isEmpty()) {
            throw new ConflictException("No permissions found.");
        }
        return permissions.stream()
                .map(this::mapPermissionToResponse)
                .toList();
    }

    @Override
    public PermissionResponse getPermissionByName(String name) {
        Permission permission = permissionDao.getPermissionByName(name);
        if (permission != null) {
            return mapPermissionToResponse(permission);
        } else {
            throw new ConflictException("Permission with name '" + name + "' does not exist.");
        }
    }

    private PermissionResponse mapPermissionToResponse(Permission permission) {
        return new PermissionResponse(permission.getId(), permission.getName(), permission.getDescription());
    }

    private Permission mapPermissionRequestToPermission(PermissionRequest permissionRequest) {
        Permission permission = new Permission();
        permission.setName(permissionRequest.getName());
        permission.setDescription(permissionRequest.getDescription());
        return permission;
    }
}
