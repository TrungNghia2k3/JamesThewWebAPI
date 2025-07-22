package com.ntn.culinary.dao;

public interface UserRolesDao {
    void assignRoleToUser(int userId, int roleId);

    void removeRoleFromUser(int userId, int roleId);

    boolean existsUserRole(int userId, int roleId);

    boolean existsRoleId(int roleId);
}
