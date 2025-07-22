package com.ntn.culinary.dao.impl;

import com.ntn.culinary.dao.PermissionDao;
import com.ntn.culinary.model.Permission;
import com.ntn.culinary.request.PermissionRequest;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static com.ntn.culinary.utils.DatabaseUtils.getConnection;

public class PermissionDaoImpl implements PermissionDao {

    @Override
    public boolean existsByName(String name) {

        String CHECK_PERMISSION_EXISTS_BY_NAME_QUERY = "SELECT 1 FROM permissions WHERE name = ? LIMIT 1";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(CHECK_PERMISSION_EXISTS_BY_NAME_QUERY)) {

            stmt.setString(1, name);

            return stmt.executeQuery().next();
        } catch (SQLException e) {
            throw new RuntimeException("SQLException: " + e.getMessage());
        }
    }

    @Override
    public void insertPermission(Permission permission) {
        String INSERT_PERMISSION_QUERY = "INSERT INTO permissions (name, description) VALUES (?)";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(INSERT_PERMISSION_QUERY)) {

            stmt.setString(1, permission.getName());
            stmt.setString(2, permission.getDescription());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("SQLException: " + e.getMessage());
        }
    }

    @Override
    public void updatePermission(Permission permission) {
        String UPDATE_PERMISSION_QUERY = "UPDATE permissions SET name = ?, description = ? WHERE id = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(UPDATE_PERMISSION_QUERY)) {

            stmt.setString(1, permission.getName());
            stmt.setString(2, permission.getDescription());
            stmt.setInt(3, permission.getId());

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("SQLException: " + e.getMessage());
        }
    }

    @Override
    public void deletePermissionById(int id) {
        String DELETE_PERMISSION_BY_ID_QUERY = "DELETE FROM permissions WHERE id = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(DELETE_PERMISSION_BY_ID_QUERY)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("SQLException: " + e.getMessage());
        }
    }

    @Override
    public List<Permission> getAllPermissions() {

        String SELECT_ALL_PERMISSIONS_QUERY = "SELECT * FROM permissions";


        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_ALL_PERMISSIONS_QUERY);
        ) {

            List<Permission> permissions = new ArrayList<>();

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Permission permission = new Permission();

                    permission.setId(rs.getInt("id"));
                    permission.setName(rs.getString("name"));
                    permission.setDescription(rs.getString("description"));

                    permissions.add(permission);
                }
                return permissions;
            }

        } catch (SQLException e) {
            throw new RuntimeException("SQLException: " + e.getMessage());
        }
    }

    @Override
    public Permission getPermissionById(int id) {

        String SELECT_PERMISSION_BY_ID_QUERY = "SELECT * FROM permissions WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_PERMISSION_BY_ID_QUERY)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Permission permission = new Permission();

                    permission.setId(rs.getInt("id"));
                    permission.setName(rs.getString("name"));
                    permission.setDescription(rs.getString("description"));

                    return permission;
                } else {
                    return null; // No permission found with the given ID
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("SQLException: " + e.getMessage());
        }
    }

    @Override
    public boolean existsById(int id) {
        String CHECK_PERMISSION_EXISTS_BY_ID_QUERY = "SELECT 1 FROM permissions WHERE id = ? LIMIT 1";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(CHECK_PERMISSION_EXISTS_BY_ID_QUERY)) {

            stmt.setInt(1, id);

            return stmt.executeQuery().next();
        } catch (SQLException e) {
            throw new RuntimeException("SQLException: " + e.getMessage());
        }
    }

    @Override
    public Permission getPermissionByName(String name) {
        String SELECT_PERMISSION_BY_NAME_QUERY = "SELECT * FROM permissions WHERE name = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_PERMISSION_BY_NAME_QUERY)) {

            stmt.setString(1, name);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Permission permission = new Permission();

                    permission.setId(rs.getInt("id"));
                    permission.setName(rs.getString("name"));
                    permission.setDescription(rs.getString("description"));

                    return permission;
                } else {
                    return null; // No permission found with the given name
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("SQLException: " + e.getMessage());
        }
    }
}
