package com.ntn.culinary.dao.impl;

import com.ntn.culinary.dao.UserDao;
import com.ntn.culinary.model.Permission;
import com.ntn.culinary.model.Role;
import com.ntn.culinary.model.User;
import org.junit.jupiter.api.*;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.List;

import static com.ntn.culinary.utils.DatabaseUtils.getConnection;
import static org.junit.jupiter.api.Assertions.*;

class UserDaoImplTest {

    private UserDao userDao;

    @BeforeAll
    static void setupDatabase() {
        System.setProperty("TEST_ENV", "true");

        try (Connection conn = getConnection()) {
            // Tạo schema
            try (Statement stmt = conn.createStatement()) {
                stmt.execute("""
                    CREATE TABLE users (
                        id INT AUTO_INCREMENT PRIMARY KEY,
                        username VARCHAR(100),
                        password VARCHAR(100),
                        email VARCHAR(100),
                        phone VARCHAR(100),
                        created_at TIMESTAMP,
                        is_active TINYINT,
                        first_name VARCHAR(100),
                        last_name VARCHAR(100),
                        avatar VARCHAR(100),
                        location VARCHAR(100),
                        school VARCHAR(100),
                        highlights TEXT,
                        experience TEXT,
                        education TEXT,
                        social_links VARCHAR(100)
                    ); 
                    CREATE TABLE roles (
                        id INT AUTO_INCREMENT PRIMARY KEY,
                        name VARCHAR(50) NOT NULL
                    );
                    CREATE TABLE user_roles (
                        user_id INT,
                        role_id INT,
                        PRIMARY KEY (user_id, role_id),
                        FOREIGN KEY (user_id) REFERENCES users(id),
                        FOREIGN KEY (role_id) REFERENCES roles(id)
                    );
                    CREATE TABLE permissions (
                        id INT AUTO_INCREMENT PRIMARY KEY,
                        name VARCHAR(50) NOT NULL,
                        description TEXT
                    );
                    CREATE TABLE staff_permissions (
                        user_id INT,
                        permission_id INT,
                        PRIMARY KEY (user_id, permission_id),
                        FOREIGN KEY (user_id) REFERENCES users(id),
                        FOREIGN KEY (permission_id) REFERENCES permissions(id)
                    );
                """);
            }

            // Chèn dữ liệu mẫu vào bảng roles
            try (PreparedStatement ps = conn.prepareStatement("""
                    INSERT INTO roles (name) VALUES (?)
                """)) {
                ps.setString(1, "ADMIN");
                ps.addBatch();
                ps.setString(1, "STAFF");
                ps.addBatch();
                ps.setString(1, "SUBSCRIBER");
                ps.addBatch();
                ps.setString(1, "GENERAL");
                ps.addBatch();
                ps.executeBatch();
            }

            // Chèn dữ liệu vào bảng permissions
            try (PreparedStatement ps = conn.prepareStatement("""
                    INSERT INTO permissions (name, description) VALUES (?, ?)
                """)) {
                ps.setString(1, "MANAGE_CONTESTS");
                ps.setString(2, "Permission to manage contests");
                ps.addBatch();
                ps.setString(1, "ANSWER_QUESTIONS");
                ps.setString(2, "Permission to answer questions");
                ps.addBatch();
                ps.setString(1, "APPROVE_ARTICLES");
                ps.setString(2, "Permission to approve articles");
                ps.addBatch();
                ps.executeBatch();
            }

            // Insert 3 user mẫu
            try (PreparedStatement ps = conn.prepareStatement("""
                    INSERT INTO users (
                        username, password, email, phone, created_at, is_active,
                        first_name, last_name, avatar, location, school,
                        highlights, experience, education, social_links
                    ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """)) {
                for (int i = 1; i <= 3; i++) {
                    ps.setString(1, "user" + i);
                    ps.setString(2, "password" + i);
                    ps.setString(3, "email" + i + "@example.com");
                    ps.setString(4, "012345678" + i);
                    ps.setTimestamp(5, Timestamp.valueOf(LocalDateTime.now()));
                    ps.setBoolean(6, true);
                    ps.setString(7, "FirstName" + i);
                    ps.setString(8, "LastName" + i);
                    ps.setString(9, "Avatar" + i);
                    ps.setString(10, "Location" + i);
                    ps.setString(11, "School" + i);
                    ps.setString(12, "Highlights" + i);
                    ps.setString(13, "Experience" + i);
                    ps.setString(14, "Education" + i);
                    ps.setString(15, "SocialLinks" + i);
                    ps.addBatch();
                }
                ps.executeBatch();
            }

            // Chèn dữ liệu vào bảng user_roles
            try (PreparedStatement ps = conn.prepareStatement("""
                    INSERT INTO user_roles (user_id, role_id) VALUES (?, ?)
                """)) {
                ps.setInt(1, 1); // user1
                ps.setInt(2, 1); // ADMIN
                ps.addBatch();
                ps.setInt(1, 2); // user2
                ps.setInt(2, 2); // STAFF
                ps.addBatch();
                ps.setInt(1, 3); // user3
                ps.setInt(2, 3); // SUBSCRIBER
                ps.addBatch();
                ps.executeBatch();
            }

            // Chèn dữ liệu vào bảng staff_permissions
            try (PreparedStatement ps = conn.prepareStatement("""
                    INSERT INTO staff_permissions (user_id, permission_id) VALUES (?, ?)
                """)) {
                ps.setInt(1, 2); // user2
                ps.setInt(2, 1); // MANAGE_CONTESTS
                ps.addBatch();
                ps.setInt(1, 2); // user2
                ps.setInt(2, 2); // ANSWER_QUESTIONS
                ps.addBatch();
                ps.setInt(1, 2); // user2
                ps.setInt(2, 3); // APPROVE_ARTICLES
                ps.addBatch();
                ps.executeBatch();
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    @BeforeEach
    void init() {
        userDao = new UserDaoImpl();
    }

    @AfterAll
    static void cleanup() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute("DELETE FROM user_roles");
            stmt.execute("DELETE FROM staff_permissions");
            stmt.execute("DELETE FROM users");
            stmt.execute("DELETE FROM roles");
            stmt.execute("DELETE FROM permissions");

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    // Test user 1
    @Test
    void testGetUserById_WhenDataExists_ReturnFirstUser() {
        // Act
        User retrieved = userDao.getUserById(1);

        // Assert
        assertNotNull(retrieved);
        assertEquals(1, retrieved.getId());
        assertEquals("user1", retrieved.getUsername());
        assertEquals("email1@example.com", retrieved.getEmail());
        assertEquals("0123456781", retrieved.getPhone());
        assertTrue(retrieved.isActive());
        assertEquals("FirstName1", retrieved.getFirstName());
        assertEquals("LastName1", retrieved.getLastName());
        assertEquals("Avatar1", retrieved.getAvatar());
        assertEquals("Location1", retrieved.getLocation());
        assertEquals("School1", retrieved.getSchool());
        assertEquals("Highlights1", retrieved.getHighlights());
        assertEquals("Experience1", retrieved.getExperience());
        assertEquals("Education1", retrieved.getEducation());
        assertEquals("SocialLinks1", retrieved.getSocialLinks());
        assertNotNull(retrieved.getCreatedAt());

        List<Role> roles = retrieved.getRoles().stream().toList();
        assertEquals(1, roles.size());
        assertEquals("ADMIN", roles.get(0).getName());

        List<Permission> permissions = retrieved.getPermissions().stream().toList();
        assertEquals(0, permissions.size()); // user1 không có quyền nào


    }

    // Test thêm user2
    @Test
    void testGetUserById_WhenDataExists_ReturnSecondUser() {
        // Act
        User retrieved = userDao.getUserById(2);

        // Assert
        assertNotNull(retrieved);
        assertEquals(2, retrieved.getId());
        assertEquals("user2", retrieved.getUsername());

        List<Role> roles = retrieved.getRoles().stream().toList();
        assertEquals(1, roles.size());
        assertEquals("STAFF", roles.get(0).getName());

        List<Permission> permissions = retrieved.getPermissions().stream().toList();
        assertEquals(3, permissions.size());
        assertEquals("APPROVE_ARTICLES", permissions.get(0).getName());
        assertEquals("MANAGE_CONTESTS", permissions.get(1).getName());
        assertEquals("ANSWER_QUESTIONS", permissions.get(2).getName());
    }

    // Test không tồn tại
    @Test
    void testGetUserById_WhenDataNotExists_ReturnNull() {
        // Act
        User retrieved = userDao.getUserById(999);

        // Assert
        assertNull(retrieved);
    }

    @Test
    void testGetAllUsers_WhenDataExists_ReturnListOfUsers() {
        // Act
        List<User> users = userDao.getAllUsers();

        // Assert
        assertNotNull(users);
        assertEquals(3, users.size());
        assertEquals("user1", users.get(0).getUsername());
        assertEquals("user2", users.get(1).getUsername());
        assertEquals("user3", users.get(2).getUsername());
    }
}
