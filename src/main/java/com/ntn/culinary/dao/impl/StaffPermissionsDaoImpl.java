package com.ntn.culinary.dao.impl;

import com.ntn.culinary.dao.StaffPermissionsDao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static com.ntn.culinary.utils.DatabaseUtils.getConnection;

public class StaffPermissionsDaoImpl implements StaffPermissionsDao {
    @Override
    public void assignPermissionToStaff(int staffId, int permissionId) {
        String INSERT_PERMISSION_QUERY = "INSERT INTO staff_permissions (staff_id, permission_id) VALUES (?, ?)";

        // Insert the new staff-permission association
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(INSERT_PERMISSION_QUERY)) {
            stmt.setInt(1, staffId);
            stmt.setInt(2, permissionId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error assigning permission to staff", e);
        }
    }

    @Override
    public void removePermissionFromStaff(int staffId, int permissionId) {

        String DELETE_PERMISSION_QUERY = "DELETE FROM staff_permissions WHERE staff_id = ? AND permission_id = ?";

        // Delete the staff-permission association
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(DELETE_PERMISSION_QUERY)) {
            stmt.setInt(1, staffId);
            stmt.setInt(2, permissionId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error removing permission from staff", e);
        }
    }

    @Override
    public boolean existsStaffPermission(int staffId, int permissionId) {

        String CHECK_PERMISSION_EXISTS_QUERY = "SELECT 1 FROM staff_permissions WHERE staff_id = ? AND permission_id = ? LIMIT 1";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(CHECK_PERMISSION_EXISTS_QUERY)) {
            stmt.setInt(1, staffId);
            stmt.setInt(2, permissionId);
            return stmt.executeQuery().next();
        } catch (SQLException e) {
            throw new RuntimeException("Error checking permission exists", e);
        }
    }

    @Override
    public boolean existsPermissionId(int permissionId) {
        String CHECK_PERMISSION_ID_EXISTS_QUERY = "SELECT 1 FROM permissions WHERE id = ? LIMIT 1";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(CHECK_PERMISSION_ID_EXISTS_QUERY)) {
            stmt.setInt(1, permissionId);
            return stmt.executeQuery().next();
        } catch (SQLException e) {
            throw new RuntimeException("Error checking permission ID exists", e);
        }
    }
}
