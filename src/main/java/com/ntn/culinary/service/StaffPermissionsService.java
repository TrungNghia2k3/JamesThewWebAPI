package com.ntn.culinary.service;

public interface StaffPermissionsService {
    void assignPermissionToStaff(int userId, int permissionId);

    void removePermissionFromStaff(int userId, int permissionId);
}
