package com.ntn.culinary.dao.impl;

import com.ntn.culinary.dao.ContestEntryDao;
import com.ntn.culinary.model.ContestEntry;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static com.ntn.culinary.utils.DatabaseUtils.getConnection;

public class ContestEntryDaoImpl implements ContestEntryDao {

    @Override
    public void addContestEntry(ContestEntry contestEntry) {

        String INSERT_CONTEST_ENTRY_QUERY = """
                INSERT INTO contest_entry (contest_id, user_id, name, ingredients, instructions, image, prepare_time, cooking_time, yield, category, area, short_description, date_created, date_modified, status)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(INSERT_CONTEST_ENTRY_QUERY)) {

            stmt.setInt(1, contestEntry.getContestId());
            stmt.setInt(2, contestEntry.getUserId());
            stmt.setString(3, contestEntry.getName());
            stmt.setString(4, contestEntry.getIngredients());
            stmt.setString(5, contestEntry.getInstructions());
            stmt.setString(6, contestEntry.getImage());
            stmt.setString(7, contestEntry.getPrepareTime());
            stmt.setString(8, contestEntry.getCookingTime());
            stmt.setString(9, contestEntry.getYield());
            stmt.setString(10, contestEntry.getCategory());
            stmt.setString(11, contestEntry.getArea());
            stmt.setString(12, contestEntry.getShortDescription());
            stmt.setDate(13, contestEntry.getDateCreated());
            stmt.setDate(14, contestEntry.getDateModified());
            stmt.setString(15, contestEntry.getStatus());

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Database: Error adding contest entry", e);
        }
    }

    @Override
    public void updateContestEntry(ContestEntry contestEntry, String imageFileName) {
        String UPDATE_CONTEST_ENTRY_QUERY = """
                UPDATE contest_entry SET name = ?, ingredients = ?, instructions = ?, image = ?, prepare_time = ?, cooking_time = ?, yield = ?, category = ?, area = ?, short_description = ?, date_modified = ? WHERE id = ?
                """;

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(UPDATE_CONTEST_ENTRY_QUERY)) {

            stmt.setString(1, contestEntry.getName());
            stmt.setString(2, contestEntry.getIngredients());
            stmt.setString(3, contestEntry.getInstructions());
            stmt.setString(4, imageFileName);
            stmt.setString(5, contestEntry.getPrepareTime());
            stmt.setString(6, contestEntry.getCookingTime());
            stmt.setString(7, contestEntry.getYield());
            stmt.setString(8, contestEntry.getCategory());
            stmt.setString(9, contestEntry.getArea());
            stmt.setString(10, contestEntry.getShortDescription());
            stmt.setDate(11, contestEntry.getDateModified());
            stmt.setInt(12, contestEntry.getId());

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error updating contest entry", e);
        }
    }

    @Override
    public int getContestEntryIdByUserIdAndContestId(int userId, int contestId) {

        String SELECT_CONTEST_ENTRY_ID_BY_USER_ID_AND_CONTEST_ID_QUERY = """
                SELECT id FROM contest_entry WHERE user_id = ? AND contest_id = ?
                """;

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_CONTEST_ENTRY_ID_BY_USER_ID_AND_CONTEST_ID_QUERY)) {

            stmt.setInt(1, userId);
            stmt.setInt(2, contestId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                } else {
                    return -1; // Not found
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving contest entry ID", e);
        }
    }

    @Override
    public int getContestEntryIdByUserIdAndContestIdAndName(int userId, int contestId, String name) {
        String SELECT_CONTEST_ENTRY_ID_BY_USER_ID_AND_CONTEST_ID_AND_NAME_QUERY = """
                SELECT id FROM contest_entry WHERE user_id = ? AND contest_id = ? AND name = ?
                """;

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_CONTEST_ENTRY_ID_BY_USER_ID_AND_CONTEST_ID_AND_NAME_QUERY)) {

            stmt.setInt(1, userId);
            stmt.setInt(2, contestId);
            stmt.setString(3, name);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                } else {
                    return -1; // Not found
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving contest entry ID by user ID, contest ID, and name", e);
        }
    }

    @Override
    public boolean existsByUserIdAndContestIdAndName(int userId, int contestId, String name) {

        String EXIST_BY_USER_ID_AND_CONTEST_ID_AND_NAME = """
                SELECT 1 FROM contest_entry WHERE user_id = ? AND contest_id = ? AND name = ? LIMIT 1
                """;

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(EXIST_BY_USER_ID_AND_CONTEST_ID_AND_NAME)) {

            stmt.setInt(1, userId);
            stmt.setInt(2, contestId);
            stmt.setString(3, name);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
                return false;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error checking contest entry existence", e);
        }
    }

    @Override
    public ContestEntry getContestEntryByUserIdAndContestIdAndName(int userId, int contestId, String name) {
        String SELECT_CONTEST_ENTRY_BY_USER_ID_AND_CONTEST_ID_AND_NAME_QUERY = """
                SELECT * FROM contest_entry WHERE user_id = ? AND contest_id = ? AND name = ? LIMIT 1
                """;

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_CONTEST_ENTRY_BY_USER_ID_AND_CONTEST_ID_AND_NAME_QUERY)) {

            stmt.setInt(1, userId);
            stmt.setInt(2, contestId);
            stmt.setString(3, name);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToContestEntry(rs);
                } else {
                    return null; // Not found
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving contest entry by user ID, contest ID, and name", e);
        }
    }

    @Override
    public void updateContestEntryStatus(int contestEntryId, String status) {

        String UPDATE_CONTEST_ENTRY_STATUS_QUERY = """
                UPDATE contest_entry SET status = ? WHERE id = ?
                """;

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(UPDATE_CONTEST_ENTRY_STATUS_QUERY)) {

            stmt.setString(1, status);
            stmt.setInt(2, contestEntryId);

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error updating contest entry status", e);
        }
    }

    @Override
    public boolean existsById(int contestEntryId) {

        String EXIST_BY_ID_QUERY = """
                SELECT 1 FROM contest_entry WHERE id = ? LIMIT 1
                """;

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(EXIST_BY_ID_QUERY)) {

            stmt.setInt(1, contestEntryId);

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error checking contest entry existence by ID", e);
        }
    }

    @Override
    public ContestEntry getContestEntryById(int contestEntryId) {

        String SELECT_CONTEST_ENTRY_BY_ID_QUERY = """
                SELECT * FROM contest_entry WHERE id = ?
                """;

        try (var conn = getConnection();
             var stmt = conn.prepareStatement(SELECT_CONTEST_ENTRY_BY_ID_QUERY)) {

            stmt.setInt(1, contestEntryId);

            try (var rs = stmt.executeQuery()) {
                if (rs.next()) {
                    ContestEntry contestEntry = new ContestEntry();
                    contestEntry.setId(rs.getInt("id"));
                    contestEntry.setContestId(rs.getInt("contest_id"));
                    contestEntry.setUserId(rs.getInt("user_id"));
                    contestEntry.setName(rs.getString("name"));
                    contestEntry.setIngredients(rs.getString("ingredients"));
                    contestEntry.setInstructions(rs.getString("instructions"));
                    contestEntry.setImage(rs.getString("image"));
                    contestEntry.setPrepareTime(rs.getString("prepare_time"));
                    contestEntry.setCookingTime(rs.getString("cooking_time"));
                    contestEntry.setYield(rs.getString("yield"));
                    contestEntry.setCategory(rs.getString("category"));
                    contestEntry.setArea(rs.getString("area"));
                    contestEntry.setShortDescription(rs.getString("short_description"));
                    contestEntry.setDateCreated(rs.getDate("date_created"));
                    contestEntry.setDateModified(rs.getDate("date_modified"));
                    contestEntry.setStatus(rs.getString("status"));

                    return contestEntry;
                } else {
                    return null; // Not found
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving contest entry by ID", e);
        }
    }

    @Override
    public void deleteContestEntryByUserIdAndContestIdAndName(int userId, int contestId, String name) {
        String DELETE_CONTEST_ENTRY_QUERY = """
                DELETE FROM contest_entry WHERE user_id = ? AND contest_id = ? AND name = ?
                """;

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(DELETE_CONTEST_ENTRY_QUERY)) {

            stmt.setInt(1, userId);
            stmt.setInt(2, contestId);
            stmt.setString(3, name);

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting contest entry by user ID and contest ID", e);
        }
    }

    @Override
    public List<ContestEntry> getContestEntryByContestId(int contestId) {
        String SELECT_CONTEST_ENTRIES_BY_CONTEST_ID_QUERY = """
                SELECT * FROM contest_entry WHERE contest_id = ?
                """;

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_CONTEST_ENTRIES_BY_CONTEST_ID_QUERY)) {

            stmt.setInt(1, contestId);

            try (ResultSet rs = stmt.executeQuery()) {
                List<ContestEntry> contestEntries = new ArrayList<>();
                while (rs.next()) {
                    contestEntries.add(mapResultSetToContestEntry(rs));
                }
                return contestEntries;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving contest entries by contest ID", e);
        }

    }

    @Override
    public List<ContestEntry> getContestEntriesByUserId(int userId) {
        String SELECT_CONTEST_ENTRIES_BY_USER_ID_QUERY = """
                SELECT * FROM contest_entry WHERE user_id = ?
                """;

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_CONTEST_ENTRIES_BY_USER_ID_QUERY)) {

            stmt.setInt(1, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                List<ContestEntry> contestEntries = new ArrayList<>();
                while (rs.next()) {
                    contestEntries.add(mapResultSetToContestEntry(rs));
                }
                return contestEntries;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving contest entries by user ID", e);
        }
    }

    @Override
    public ContestEntry getContestEntryByUserIdAndContestId(int userId, int contestId) {
        String SELECT_CONTEST_ENTRY_BY_USER_ID_AND_CONTEST_ID_QUERY = """
                SELECT * FROM contest_entry WHERE user_id = ? AND contest_id = ?
                """;

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_CONTEST_ENTRY_BY_USER_ID_AND_CONTEST_ID_QUERY)) {

            stmt.setInt(1, userId);
            stmt.setInt(2, contestId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToContestEntry(rs);
                } else {
                    return null; // Not found
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving contest entry by user ID and contest ID", e);
        }
    }

    @Override
    public boolean existsByNameAndContestId(String name, int contestId) {
        String EXISTS_BY_NAME_AND_CONTEST_ID_QUERY = """
                SELECT 1 FROM contest_entry WHERE name = ? AND contest_id = ? LIMIT 1
                """;

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(EXISTS_BY_NAME_AND_CONTEST_ID_QUERY)) {

            stmt.setString(1, name);
            stmt.setInt(2, contestId);

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error checking existence of contest entry by name and contest ID", e);
        }
    }

    @Override
    public boolean existsContestEntryWithNameExcludingId(String name, int contestId, int contestEntryId) {
        String EXISTS_CONTEST_ENTRY_WITH_NAME_EXCLUDING_ID_QUERY = """
                SELECT 1 FROM contest_entry WHERE name = ? AND contest_id = ? AND id != ? LIMIT 1
                """;

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(EXISTS_CONTEST_ENTRY_WITH_NAME_EXCLUDING_ID_QUERY)) {

            stmt.setString(1, name);
            stmt.setInt(2, contestId);
            stmt.setInt(3, contestEntryId);

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error checking existence of contest entry with name excluding ID", e);
        }
    }

    private ContestEntry mapResultSetToContestEntry(ResultSet rs) throws SQLException {
        ContestEntry contestEntry = new ContestEntry();
        contestEntry.setId(rs.getInt("id"));
        contestEntry.setContestId(rs.getInt("contest_id"));
        contestEntry.setUserId(rs.getInt("user_id"));
        contestEntry.setName(rs.getString("name"));
        contestEntry.setIngredients(rs.getString("ingredients"));
        contestEntry.setInstructions(rs.getString("instructions"));
        contestEntry.setImage(rs.getString("image"));
        contestEntry.setPrepareTime(rs.getString("prepare_time"));
        contestEntry.setCookingTime(rs.getString("cooking_time"));
        contestEntry.setYield(rs.getString("yield"));
        contestEntry.setCategory(rs.getString("category"));
        contestEntry.setArea(rs.getString("area"));
        contestEntry.setShortDescription(rs.getString("short_description"));
        contestEntry.setDateCreated(rs.getDate("date_created"));
        contestEntry.setDateModified(rs.getDate("date_modified"));
        contestEntry.setStatus(rs.getString("status"));

        return contestEntry;
    }
}
