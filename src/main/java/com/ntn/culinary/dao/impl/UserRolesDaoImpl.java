package com.ntn.culinary.dao.impl;

import com.ntn.culinary.dao.UserRolesDao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static com.ntn.culinary.utils.DatabaseUtils.getConnection;

public class UserRolesDaoImpl implements UserRolesDao {
    @Override
    public void assignRoleToUser(int userId, int roleId) {
        String INSERT_USER_ROLE_QUERY = "INSERT INTO user_roles (user_id, role_id) VALUES (?, ?)";

        // var là Local Variable Type Inference (Java 10+).
        //Tức là compiler tự hiểu kiểu dữ liệu, bạn không cần ghi rõ.

        // Cách 1: Viết rõ
        //Connection conn = getConnection();
        //PreparedStatement stmt = conn.prepareStatement(...);

        //Cách 2: Dùng var
        //var conn = getConnection();
        //var stmt = conn.prepareStatement(...);

        //Không khác nhau về:
        //Performance
        // Kiểu dữ liệu thực sự
        // Cách hoạt động

        // Lưu ý khi dùng var
        //var chỉ dùng được cho biến local:
        // Trong method
        // Trong initializer block

        // Insert the new user-role association
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(INSERT_USER_ROLE_QUERY)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, roleId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error assigning role to user", e);
        }
    }

    @Override
    public void removeRoleFromUser(int userId, int roleId) {
        String DELETE_USER_ROLE_QUERY = "DELETE FROM user_roles WHERE user_id = ? AND role_id = ?";

        // Delete the user-role association
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(DELETE_USER_ROLE_QUERY)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, roleId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error removing role from user", e);
        }
    }

    @Override
    public boolean existsUserRole(int userId, int roleId) {
        String CHECK_USER_ROLE_EXISTS_QUERY = "SELECT 1 FROM user_roles WHERE user_id = ? AND role_id = ? LIMIT 1";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(CHECK_USER_ROLE_EXISTS_QUERY)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, roleId);
            return stmt.executeQuery().next();
        } catch (SQLException e) {
            throw new RuntimeException("Error checking user role", e);
        }
    }

    @Override
    public boolean existsRoleId(int roleId) {
        String CHECK_ROLE_ID_EXISTS_QUERY = "SELECT 1 FROM roles WHERE id = ? LIMIT 1";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(CHECK_ROLE_ID_EXISTS_QUERY)) {
            stmt.setInt(1, roleId);
            return stmt.executeQuery().next();
        } catch (SQLException e) {
            throw new RuntimeException("Error checking role ID exists", e);
        }
    }
}



