package com.ntn.culinary.dao.impl;

import com.ntn.culinary.dao.ContestDao;
import com.ntn.culinary.utils.DatabaseUtils;
import com.ntn.culinary.model.Contest;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ContestDaoImpl implements ContestDao {

    @Override
    public List<Contest> getAllContests() {

        String SELECT_ALL_CONTEST = "SELECT * FROM contests";

        try (Connection conn = DatabaseUtils.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_ALL_CONTEST)) {

            List<Contest> contests = new ArrayList<>();

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Contest contest = new Contest();
                    contest.setId(rs.getInt("id"));
                    contest.setArticleBody(rs.getString("article_body"));
                    contest.setHeadline(rs.getString("headline"));
                    contest.setDescription(rs.getString("description"));
                    contest.setDatePublished(rs.getDate("date_published"));
                    contest.setDateModified(rs.getDate("date_modified"));
                    contest.setPrize(rs.getString("prize"));
                    contest.setFree(rs.getBoolean("is_free"));
                    contest.setClosed(rs.getBoolean("is_closed"));
                    contest.setAccessRole(rs.getString("access_role"));
                    contests.add(contest);
                }
                return contests;
            }
        } catch (SQLException ex) {
            throw new RuntimeException("SQLException: " + ex.getMessage());
        }
    }

    @Override
    public Contest getContestById(int id) {

        String SELECT_CONTEST_BY_ID = "SELECT * FROM contests WHERE id = ?";

        try (Connection conn = DatabaseUtils.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_CONTEST_BY_ID)) {
            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery();) {
                if (rs.next()) {
                    Contest contest = new Contest();
                    contest.setId(rs.getInt("id"));
                    contest.setArticleBody(rs.getString("article_body"));
                    contest.setHeadline(rs.getString("headline"));
                    contest.setDescription(rs.getString("description"));
                    contest.setDatePublished(rs.getDate("date_published"));
                    contest.setDateModified(rs.getDate("date_modified"));
                    contest.setPrize(rs.getString("prize"));
                    contest.setFree(rs.getBoolean("is_free"));
                    contest.setClosed(rs.getBoolean("is_closed"));
                    contest.setAccessRole(rs.getString("access_role"));

                    return contest;
                } else {
                    return null;
                }
            }
        } catch (SQLException ex) {
            throw new RuntimeException("SQLException: " + ex.getMessage());
        }
    }

    @Override
    public boolean existsById(int id) {

        String EXIST_BY_ID_QUERY = "SELECT 1 FROM contests WHERE id = ? LIMIT 1";

        try (Connection conn = DatabaseUtils.getConnection();
             PreparedStatement stmt = conn.prepareStatement(EXIST_BY_ID_QUERY)) {

            stmt.setInt(1, id);
            return stmt.executeQuery().next();
        } catch (SQLException ex) {
            throw new RuntimeException("SQLException: " + ex.getMessage());
        }
    }

    @Override
    public void addContest(Contest contest) {
        String INSERT_CONTEST_QUERY = """
                INSERT INTO contests (article_body, headline, description, date_published, date_modified, prize, is_free, is_closed, access_role)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection conn = DatabaseUtils.getConnection();
             PreparedStatement stmt = conn.prepareStatement(INSERT_CONTEST_QUERY)) {

            stmt.setString(1, contest.getArticleBody());
            stmt.setString(2, contest.getHeadline());
            stmt.setString(3, contest.getDescription());
            stmt.setDate(4, contest.getDatePublished());
            stmt.setDate(5, contest.getDateModified());
            stmt.setString(6, contest.getPrize());
            stmt.setBoolean(7, contest.isFree());
            stmt.setBoolean(8, contest.isClosed());
            stmt.setString(9, contest.getAccessRole());

            stmt.executeUpdate();
        } catch (SQLException ex) {
            throw new RuntimeException("SQLException: " + ex.getMessage());
        }
    }

    @Override
    public void updateContest(Contest contest) {
        String UPDATE_CONTEST_QUERY = """
                UPDATE contests
                SET article_body = ?, headline = ?, description = ?, date_published = ?, date_modified = ?, prize = ?, is_free = ?, is_closed = ?, access_role = ?
                WHERE id = ?
                """;

        try (Connection conn = DatabaseUtils.getConnection();
             PreparedStatement stmt = conn.prepareStatement(UPDATE_CONTEST_QUERY)) {

            stmt.setString(1, contest.getArticleBody());
            stmt.setString(2, contest.getHeadline());
            stmt.setString(3, contest.getDescription());
            stmt.setDate(4, contest.getDatePublished());
            stmt.setDate(5, contest.getDateModified());
            stmt.setString(6, contest.getPrize());
            stmt.setBoolean(7, contest.isFree());
            stmt.setBoolean(8, contest.isClosed());
            stmt.setString(9, contest.getAccessRole());
            stmt.setInt(10, contest.getId());

            stmt.executeUpdate();
        } catch (SQLException ex) {
            throw new RuntimeException("SQLException: " + ex.getMessage());
        }

    }

    @Override
    public void updateContestStatus(int id, boolean isClosed) {
        String UPDATE_CONTEST_STATUS_QUERY = "UPDATE contests SET is_closed = ? WHERE id = ?";

        try (Connection conn = DatabaseUtils.getConnection();
             PreparedStatement stmt = conn.prepareStatement(UPDATE_CONTEST_STATUS_QUERY)) {

            stmt.setBoolean(1, isClosed);
            stmt.setInt(2, id);
            stmt.executeUpdate();
        } catch (SQLException ex) {
            throw new RuntimeException("SQLException: " + ex.getMessage());
        }

    }

    @Override
    public void deleteContestById(int id) {
        String DELETE_CONTEST_BY_ID_QUERY = "DELETE FROM contests WHERE id = ?";

        try (Connection conn = DatabaseUtils.getConnection();
             PreparedStatement stmt = conn.prepareStatement(DELETE_CONTEST_BY_ID_QUERY)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException ex) {
            throw new RuntimeException("SQLException: " + ex.getMessage());
        }

    }

    @Override
    public int getContestIdByHeadline(String headline) {
        String SELECT_CONTEST_ID_BY_HEADLINE = "SELECT id FROM contests WHERE headline = ?";

        try (Connection conn = DatabaseUtils.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_CONTEST_ID_BY_HEADLINE)) {
            stmt.setString(1, headline);

            try (ResultSet rs = stmt.executeQuery();) {
                if (rs.next()) {
                    return rs.getInt("id");
                } else {
                    return -1; // or throw an exception if preferred
                }
            }
        } catch (SQLException ex) {
            throw new RuntimeException("SQLException: " + ex.getMessage());
        }
    }

    @Override
    public boolean existsByHeadline(String headline) {
        String EXIST_BY_HEADLINE_QUERY = "SELECT 1 FROM contests WHERE headline = ? LIMIT 1";

        try (Connection conn = DatabaseUtils.getConnection();
             PreparedStatement stmt = conn.prepareStatement(EXIST_BY_HEADLINE_QUERY)) {

            stmt.setString(1, headline);
            return stmt.executeQuery().next();
        } catch (SQLException ex) {
            throw new RuntimeException("SQLException: " + ex.getMessage());
        }
    }

    @Override
    public boolean isContestClosed(int id) {
        String IS_CONTEST_CLOSED_QUERY = "SELECT is_closed FROM contests WHERE id = ?";

        try (Connection conn = DatabaseUtils.getConnection();
             PreparedStatement stmt = conn.prepareStatement(IS_CONTEST_CLOSED_QUERY)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getBoolean("is_closed");
                } else {
                    return false; // or throw an exception if preferred
                }
            }
        } catch (SQLException ex) {
            throw new RuntimeException("SQLException: " + ex.getMessage());
        }
    }
}