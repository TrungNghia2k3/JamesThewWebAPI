package com.ntn.culinary.request;

public class StaffPermissionsRequest {
    private int userId;
    private int permissionId;

    public StaffPermissionsRequest() {
    }

    public StaffPermissionsRequest(int userId, int permissionId) {
        this.userId = userId;
        this.permissionId = permissionId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getPermissionId() {
        return permissionId;
    }

    public void setPermissionId(int permissionId) {
        this.permissionId = permissionId;
    }
}
