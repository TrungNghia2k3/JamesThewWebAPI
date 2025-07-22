package com.ntn.culinary.dao.impl;

import com.ntn.culinary.dao.RoleDao;
import com.ntn.culinary.model.Role;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static com.ntn.culinary.utils.DatabaseUtils.getConnection;

public class RoleDaoImpl implements RoleDao {

    @Override
    public boolean existsByName(String name) {

        String CHECK_ROLE_EXISTS_BY_NAME_QUERY = "SELECT 1 FROM roles WHERE name = ? LIMIT 1";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(CHECK_ROLE_EXISTS_BY_NAME_QUERY)) {

            stmt.setString(1, name);
            return stmt.executeQuery().next();

        } catch (SQLException e) {
            throw new RuntimeException("SQL Exception: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean existsById(int id) {

        final String EXISTS_ROLE_BY_ID_QUERY = "SELECT 1 FROM roles WHERE id = ? LIMIT 1";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(EXISTS_ROLE_BY_ID_QUERY)) {

            stmt.setInt(1, id);
            return stmt.executeQuery().next();
        } catch (SQLException ex) {
            throw new RuntimeException("SQL Exception: " + ex.getMessage(), ex);
        }
    }

    @Override
    public List<Role> getAllRoles() {

        String SELECT_ALL_ROLES_QUERY = "SELECT * FROM roles";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_ALL_ROLES_QUERY)) {

            List<Role> roles = new ArrayList<>();

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Role role = new Role();
                    role.setId(rs.getInt("id"));
                    role.setName(rs.getString("name"));
                    roles.add(role);
                }
                return roles;
            }
        } catch (SQLException ex) {
            throw new RuntimeException("SQL Exception: " + ex.getMessage(), ex);
        }
    }

    @Override
    public Role getRoleById(int id) {

        String SELECT_ROLE_BY_ID_QUERY = "SELECT * FROM roles WHERE id = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_ROLE_BY_ID_QUERY)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Role role = new Role();
                    role.setId(rs.getInt("id"));
                    role.setName(rs.getString("name"));
                    return role;
                } else {
                    return null; // No role found with the given ID
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("SQL Exception: " + e.getMessage(), e);
        }
    }

    @Override
    public void addRole(String name) {
        String INSERT_ROLE_QUERY = "INSERT INTO roles (name) VALUES (?)";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(INSERT_ROLE_QUERY)) {

            stmt.setString(1, name);
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("SQL Exception: " + e.getMessage(), e);
        }
    }

    @Override
    public void updateRole(Role role) {
        String UPDATE_ROLE_QUERY = "UPDATE roles SET name = ? WHERE id = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(UPDATE_ROLE_QUERY)) {

            stmt.setString(1, role.getName());
            stmt.setInt(2, role.getId());
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("SQL Exception: " + e.getMessage(), e);
        }
    }

    @Override
    public void deleteRoleById(int id) {
        String DELETE_ROLE_BY_ID_QUERY = "DELETE FROM roles WHERE id = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(DELETE_ROLE_BY_ID_QUERY)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("SQL Exception: " + e.getMessage(), e);
        }
    }

    @Override
    public Role getRoleByName(String name) {
        String SELECT_ROLE_BY_NAME_QUERY = "SELECT * FROM roles WHERE name = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_ROLE_BY_NAME_QUERY)) {

            stmt.setString(1, name);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Role role = new Role();
                    role.setId(rs.getInt("id"));
                    role.setName(rs.getString("name"));
                    return role;
                } else {
                    return null; // No role found with the given name
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("SQL Exception: " + e.getMessage(), e);
        }
    }
}
