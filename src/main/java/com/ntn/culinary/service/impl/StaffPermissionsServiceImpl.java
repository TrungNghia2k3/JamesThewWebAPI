package com.ntn.culinary.service.impl;

import com.ntn.culinary.dao.PermissionDao;
import com.ntn.culinary.dao.RoleDao;
import com.ntn.culinary.dao.StaffPermissionsDao;
import com.ntn.culinary.dao.UserDao;
import com.ntn.culinary.exception.NotFoundException;
import com.ntn.culinary.service.StaffPermissionsService;

public class StaffPermissionsServiceImpl implements StaffPermissionsService {

    private final StaffPermissionsDao staffPermissionsDao;
    private final UserDao userDao;
    private final PermissionDao permissionDao;

    public StaffPermissionsServiceImpl(StaffPermissionsDao staffPermissionsDao, UserDao userDao, PermissionDao permissionDao) {
        this.staffPermissionsDao = staffPermissionsDao;
        this.userDao = userDao;
        this.permissionDao = permissionDao;
    }

    @Override
    public void assignPermissionToStaff(int userId, int permissionId) {
        if (!userDao.existsById(userId)) {
            throw new NotFoundException("User does not exist");
        }
        if (!permissionDao.existsById(permissionId)) {
            throw new NotFoundException("Permission does not exist");
        }
        staffPermissionsDao.assignPermissionToStaff(userId, permissionId);
    }

    @Override
    public void removePermissionFromStaff(int userId, int permissionId) {
        if (!userDao.existsById(userId)) {
            throw new NotFoundException("User does not exist");
        }

        if (!permissionDao.existsById(permissionId)) {
            throw new NotFoundException("Permission does not exist");
        }

        if (!staffPermissionsDao.existsStaffPermission(userId, permissionId)) {
            throw new NotFoundException("Permission not assigned to user");
        }

        staffPermissionsDao.removePermissionFromStaff(userId, permissionId);
    }
}
