package com.ntn.culinary.dao.impl;

import com.ntn.culinary.dao.AnnouncementDao;
import com.ntn.culinary.model.Announcement;

import static com.ntn.culinary.utils.DatabaseUtils.getConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AnnouncementDaoImpl implements AnnouncementDao {

    @Override
    public void insertAnnouncement(Announcement announcement) {

        String INSERT_ANNOUNCEMENT_QUERY = "INSERT INTO announcements (title, announcement_date, description, contest_id) VALUES (?, ?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(INSERT_ANNOUNCEMENT_QUERY)) {
            stmt.setString(1, announcement.getTitle());
            stmt.setDate(2, announcement.getAnnouncementDate());
            stmt.setString(3, announcement.getDescription());
            stmt.setInt(4, announcement.getContestId());

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error inserting announcement: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean existsAnnouncementWithContest(int contestId) {
        String EXIST_ANNOUNCEMENT_WITH_CONTEST_QUERY = "SELECT 1 FROM announcements WHERE contest_id = ? LIMIT 1";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(EXIST_ANNOUNCEMENT_WITH_CONTEST_QUERY)) {
            stmt.setInt(1, contestId);
            return stmt.executeQuery().next();
        } catch (SQLException e) {
            throw new RuntimeException("Error checking announcement existence: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean existsAnnouncementById(int id) {
        String EXIST_ANNOUNCEMENT_BY_ID_QUERY = "SELECT 1 FROM announcements WHERE id = ? LIMIT 1";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(EXIST_ANNOUNCEMENT_BY_ID_QUERY)) {
            stmt.setInt(1, id);
            return stmt.executeQuery().next();
        } catch (SQLException e) {
            throw new RuntimeException("Error checking announcement existence by ID: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<Integer> getAnnouncementIdByContestId(int contestId) {
        String SELECT_ANNOUNCEMENT_ID_BY_CONTEST_QUERY = "SELECT id FROM announcements WHERE contest_id = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_ANNOUNCEMENT_ID_BY_CONTEST_QUERY)) {

            stmt.setInt(1, contestId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(rs.getInt("id"));
                } else {
                    return Optional.empty();
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving announcement ID: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Announcement> getAllAnnouncements() {

        String SELECT_ALL_ANNOUNCEMENTS_QUERY = "SELECT * FROM announcements";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_ALL_ANNOUNCEMENTS_QUERY)) {

            List<Announcement> announcements = new ArrayList<>();

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Announcement announcement = new Announcement();
                    announcement.setId(rs.getInt("id"));
                    announcement.setTitle(rs.getString("title"));
                    announcement.setAnnouncementDate(rs.getDate("announcement_date"));
                    announcement.setDescription(rs.getString("description"));
                    announcement.setContestId(rs.getInt("contest_id"));
                    announcements.add(announcement);
                }
                return announcements;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving all announcements: " + e.getMessage(), e);
        }
    }

    @Override
    public Announcement getAnnouncementById(int id) {
        String SELECT_ANNOUNCEMENT_BY_ID_QUERY = "SELECT * FROM announcements WHERE id = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_ANNOUNCEMENT_BY_ID_QUERY)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Announcement announcement = new Announcement();
                    announcement.setId(rs.getInt("id"));
                    announcement.setTitle(rs.getString("title"));
                    announcement.setAnnouncementDate(rs.getDate("announcement_date"));
                    announcement.setDescription(rs.getString("description"));
                    announcement.setContestId(rs.getInt("contest_id"));
                    return announcement;
                } else {
                    return null;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving announcement: " + e.getMessage(), e);
        }
    }

    @Override
    public void updateAnnouncement(Announcement announcement) {

        String UPDATE_ANNOUNCEMENT_QUERY = "UPDATE announcements SET title = ?, description = ?, announcement_date = ? WHERE id = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(UPDATE_ANNOUNCEMENT_QUERY)) {
            stmt.setString(1, announcement.getTitle());
            stmt.setString(2, announcement.getDescription());
            stmt.setDate(3, announcement.getAnnouncementDate());
            stmt.setInt(4, announcement.getId());

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error updating announcement: " + e.getMessage(), e);
        }
    }

    @Override
    public void deleteAnnouncementById(int id) {

        String DELETE_ANNOUNCEMENT_QUERY = "DELETE FROM announcements WHERE id = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(DELETE_ANNOUNCEMENT_QUERY)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting announcement: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean existsAnnouncementWithTitle(String title) {
        String EXIST_ANNOUNCEMENT_WITH_TITLE_QUERY = "SELECT 1 FROM announcements WHERE title = ? LIMIT 1";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(EXIST_ANNOUNCEMENT_WITH_TITLE_QUERY)) {
            stmt.setString(1, title);
            return stmt.executeQuery().next();
        } catch (SQLException e) {
            throw new RuntimeException("Error checking announcement existence by title: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean existsAnnouncementWithTitleExcludingId(int id, String title) {
        String EXIST_ANNOUNCEMENT_WITH_TITLE_EXCLUDING_ID_QUERY = "SELECT 1 FROM announcements WHERE title = ? AND id != ? LIMIT 1";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(EXIST_ANNOUNCEMENT_WITH_TITLE_EXCLUDING_ID_QUERY)) {
            stmt.setString(1, title);
            stmt.setInt(2, id);
            return stmt.executeQuery().next();
        } catch (SQLException e) {
            throw new RuntimeException("Error checking announcement existence by title excluding ID: " + e.getMessage(), e);
        }
    }
}
