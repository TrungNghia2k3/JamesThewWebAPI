package com.ntn.culinary.dao;

public interface StaffPermissionsDao {
    void assignPermissionToStaff(int staffId, int permissionId);

    void removePermissionFromStaff(int staffId, int permissionId);

    boolean existsStaffPermission(int staffId, int permissionId);

    boolean existsPermissionId(int permissionId);
}
