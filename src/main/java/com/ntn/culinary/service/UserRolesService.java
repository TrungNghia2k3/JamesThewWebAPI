package com.ntn.culinary.service;

public interface UserRolesService {
    void assignRoleToUser(int userId, int roleId);

    void removeRoleFromUser(int userId, int roleId);
}
