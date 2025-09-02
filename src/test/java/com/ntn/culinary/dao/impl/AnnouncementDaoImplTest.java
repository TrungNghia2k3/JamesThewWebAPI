package com.ntn.culinary.dao.impl;

import com.ntn.culinary.dao.AnnouncementDao;
import com.ntn.culinary.model.Announcement;
import com.ntn.culinary.utils.DatabaseUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.*;
import java.util.List;

import static com.ntn.culinary.utils.DatabaseUtils.getConnection;
import static org.junit.jupiter.api.Assertions.*;

class AnnouncementDaoImplTest {
    private AnnouncementDao announcementDao;

    @BeforeAll
    static void setupDatabase() throws SQLException {
        // Set flag để DatabaseUtils load H2
        System.setProperty("TEST_ENV", "true");

        // Tạo schema
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute("""
                        CREATE TABLE announcements (
                            id INT AUTO_INCREMENT PRIMARY KEY NOT NULL,
                            title VARCHAR(255) NOT NULL,
                            description TEXT NOT NULL,
                            announcement_date DATE NOT NULL,
                            contest_id INT NOT NULL
                        )
                    """);
        }
    }

    @BeforeEach
    void init() {
        announcementDao = new AnnouncementDaoImpl();
    }

    @AfterEach
    void cleanup() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute("DELETE FROM announcements");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    @Test
    void testInsertAnnouncement_Success() {
        // Arrange
        Announcement ann = new Announcement();
        ann.setTitle("Test Announcement");
        ann.setAnnouncementDate(java.sql.Date.valueOf("2025-07-06"));
        ann.setDescription("Test Description");
        ann.setContestId(1);

        // Act
        announcementDao.insertAnnouncement(ann);

        // Assert
        String query = "SELECT * FROM announcements WHERE title = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, "Test Announcement");
            ResultSet rs = stmt.executeQuery();
            rs.next();
            int count = rs.getInt(1);
            assertEquals(1, count);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    @Test
    void testInsertAnnouncement_Failure() {
        // Arrange
        Announcement ann = new Announcement();
        ann.setTitle(null); // Invalid title
        ann.setAnnouncementDate(java.sql.Date.valueOf("2025-07-06"));
        ann.setDescription("Test Description");
        ann.setContestId(1);

        // Act & Assert
        assertThrows(RuntimeException.class, () -> announcementDao.insertAnnouncement(ann));
    }

    @Test
    void testExistsAnnouncementWithContest_Success() {
        // Arrange
        try (Connection conn = DatabaseUtils.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "INSERT INTO announcements (title, announcement_date, description, contest_id) VALUES (?, ?, ?, ?)")) {
            stmt.setString(1, "Test Announcement");
            stmt.setDate(2, Date.valueOf("2025-07-06"));
            stmt.setString(3, "Test Description");
            stmt.setInt(4, 1);
            stmt.executeUpdate();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        // Act
        boolean exists = announcementDao.existsAnnouncementWithContest(1);

        // Assert
        assertTrue(exists);
    }

    @Test
    void testExistsAnnouncementWithContest_Failure() {
        // Act
        boolean exists = announcementDao.existsAnnouncementWithContest(999); // Non-existent contest ID

        // Assert
        assertFalse(exists);
    }

    @Test
    void testGetAllAnnouncements_Success() {
        // Arrange
        try (Connection conn = DatabaseUtils.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "INSERT INTO announcements (title, announcement_date, description, contest_id) VALUES (?, ?, ?, ?)")) {
            stmt.setString(1, "Announcement 1");
            stmt.setDate(2, Date.valueOf("2023-01-01"));
            stmt.setString(3, "Description 1");
            stmt.setInt(4, 10);
            stmt.executeUpdate();

            stmt.setString(1, "Announcement 2");
            stmt.setDate(2, Date.valueOf("2023-02-02"));
            stmt.setString(3, "Description 2");
            stmt.setInt(4, 20);
            stmt.executeUpdate();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        // Act
        List<Announcement> result = announcementDao.getAllAnnouncements();

        // Assert
        assertEquals(2, result.size());

        Announcement a1 = result.get(0);
        assertEquals("Announcement 1", a1.getTitle());
        assertEquals(Date.valueOf("2023-01-01"), a1.getAnnouncementDate());
        assertEquals("Description 1", a1.getDescription());
        assertEquals(10, a1.getContestId());

        Announcement a2 = result.get(1);
        assertEquals("Announcement 2", a2.getTitle());
        assertEquals(Date.valueOf("2023-02-02"), a2.getAnnouncementDate());
        assertEquals("Description 2", a2.getDescription());
        assertEquals(20, a2.getContestId());
    }

    @Test
    void testGetAllAnnouncements_Empty() {
        // Act
        List<Announcement> result = announcementDao.getAllAnnouncements();

        // Assert
        assertTrue(result.isEmpty());
    }
}