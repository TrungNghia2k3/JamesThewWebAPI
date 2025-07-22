package com.ntn.culinary.service;

import com.ntn.culinary.request.RoleRequest;
import com.ntn.culinary.response.RoleResponse;

import java.util.List;

public interface RoleService {
    void addRole(String name);

    void updateRole(RoleRequest roleRequest);

    RoleResponse getRoleById(int id);

    void deleteRole(int id);

    List<RoleResponse> getAllRoles();

    RoleResponse getRoleByName(String name);
}
