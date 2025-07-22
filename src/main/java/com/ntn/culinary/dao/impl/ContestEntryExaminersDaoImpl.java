package com.ntn.culinary.dao.impl;

import com.ntn.culinary.dao.ContestEntryExaminersDao;
import com.ntn.culinary.model.ContestEntryExaminers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import static com.ntn.culinary.utils.DatabaseUtils.getConnection;

public class ContestEntryExaminersDaoImpl implements ContestEntryExaminersDao {

    @Override
    public void addContestEntryExaminer(ContestEntryExaminers contestEntryExaminers) {

        String INSERT_CONTEST_ENTRY_EXAMINER_QUERY = """
                INSERT INTO contest_entry_examiners (contest_entry_id, examiner_id, score, feedback, exam_date)
                VALUES (?, ?, ?, ?, ?)
                """;

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(INSERT_CONTEST_ENTRY_EXAMINER_QUERY)) {

            stmt.setInt(1, contestEntryExaminers.getContestEntryId());
            stmt.setInt(2, contestEntryExaminers.getExaminerId());
            stmt.setDouble(3, contestEntryExaminers.getScore());
            stmt.setString(4, contestEntryExaminers.getFeedback());
            stmt.setTimestamp(5, contestEntryExaminers.getExamDate());

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error adding contest entry examiner", e);
        }
    }

    @Override
    public void updateContestEntryExaminer(ContestEntryExaminers contestEntryExaminers) {
        String UPDATE_CONTEST_ENTRY_EXAMINER_QUERY = """
                UPDATE contest_entry_examiners
                SET score = ?, feedback = ?, exam_date = ?
                WHERE contest_entry_id = ? AND examiner_id = ?
                """;

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(UPDATE_CONTEST_ENTRY_EXAMINER_QUERY)) {

            stmt.setDouble(1, contestEntryExaminers.getScore());
            stmt.setString(2, contestEntryExaminers.getFeedback());
            stmt.setTimestamp(3, contestEntryExaminers.getExamDate());
            stmt.setInt(4, contestEntryExaminers.getContestEntryId());
            stmt.setInt(5, contestEntryExaminers.getExaminerId());

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error updating contest entry examiner", e);
        }
    }

    @Override
    public boolean existsById(int id) {
        String EXIST_BY_ID_QUERY = """
                SELECT 1 FROM contest_entry_examiners WHERE id = ? LIMIT 1
                """;

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(EXIST_BY_ID_QUERY)) {

            stmt.setInt(1, id);

            return stmt.executeQuery().next();
        } catch (SQLException e) {
            throw new RuntimeException("Error checking existence of contest entry examiner by ID", e);
        }
    }

    @Override
    public List<ContestEntryExaminers> getAllContestEntryExaminersByExaminerId(int examinerId) {
        String GET_ALL_CONTEST_ENTRY_EXAMINERS_BY_EXAMINER_ID_QUERY = """
                SELECT * FROM contest_entry_examiners WHERE examiner_id = ?
                """;

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(GET_ALL_CONTEST_ENTRY_EXAMINERS_BY_EXAMINER_ID_QUERY)) {

            stmt.setInt(1, examinerId);
            ResultSet rs = stmt.executeQuery();

            List<ContestEntryExaminers> examinersList = new java.util.ArrayList<>();
            while (rs.next()) {
                ContestEntryExaminers examiner = new ContestEntryExaminers();
                examiner.setId(rs.getInt("id"));
                examiner.setContestEntryId(rs.getInt("contest_entry_id"));
                examiner.setExaminerId(rs.getInt("examiner_id"));
                examiner.setScore(rs.getInt("score"));
                examiner.setFeedback(rs.getString("feedback"));
                examiner.setExamDate(rs.getTimestamp("exam_date"));
                examinersList.add(examiner);
            }
            return examinersList;
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving all contest entry examiners by examiner ID", e);
        }
    }

    @Override
    public ContestEntryExaminers getContestEntryExaminerById(int id) {
        String GET_CONTEST_ENTRY_EXAMINER_BY_ID_QUERY = """
                SELECT * FROM contest_entry_examiners WHERE id = ?
                """;

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(GET_CONTEST_ENTRY_EXAMINER_BY_ID_QUERY)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                ContestEntryExaminers examiner = new ContestEntryExaminers();
                examiner.setId(rs.getInt("id"));
                examiner.setContestEntryId(rs.getInt("contest_entry_id"));
                examiner.setExaminerId(rs.getInt("examiner_id"));
                examiner.setScore(rs.getInt("score"));
                examiner.setFeedback(rs.getString("feedback"));
                examiner.setExamDate(rs.getTimestamp("exam_date"));
                return examiner;
            } else {
                return null; // No entry found
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving contest entry examiner by ID", e);
        }
    }

    @Override
    public List<ContestEntryExaminers> getContestEntryExaminersByContestEntryId(int contestEntryId) {
        String GET_CONTEST_ENTRY_EXAMINERS_BY_CONTEST_ENTRY_ID_QUERY = """
                SELECT * FROM contest_entry_examiners WHERE contest_entry_id = ?
                """;

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(GET_CONTEST_ENTRY_EXAMINERS_BY_CONTEST_ENTRY_ID_QUERY)) {

            stmt.setInt(1, contestEntryId);
            ResultSet rs = stmt.executeQuery();

            List<ContestEntryExaminers> examinersList = new java.util.ArrayList<>();
            while (rs.next()) {
                ContestEntryExaminers examiner = new ContestEntryExaminers();
                examiner.setId(rs.getInt("id"));
                examiner.setContestEntryId(rs.getInt("contest_entry_id"));
                examiner.setExaminerId(rs.getInt("examiner_id"));
                examiner.setScore(rs.getInt("score"));
                examiner.setFeedback(rs.getString("feedback"));
                examiner.setExamDate(rs.getTimestamp("exam_date"));
                examinersList.add(examiner);
            }
            return examinersList;
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving contest entry examiners by contest entry ID", e);
        }
    }

    @Override
    public void deleteByContestEntryIdAndExaminerId(int contestEntryId, int examinerId) {
        String DELETE_CONTEST_ENTRY_EXAMINER_QUERY = """
                DELETE FROM contest_entry_examiners WHERE contest_entry_id = ? AND examiner_id = ?
                """;

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(DELETE_CONTEST_ENTRY_EXAMINER_QUERY)) {

            stmt.setInt(1, contestEntryId);
            stmt.setInt(2, examinerId);

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting contest entry examiner", e);
        }
    }

    @Override
    public boolean existsByContestEntryIdAndExaminerId(int contestEntryId, int examinerId) {

        String EXIST_BY_CONTEST_ENTRY_ID_AND_EXAMINER_ID_QUERY = """
                SELECT 1 FROM contest_entry_examiners WHERE contest_entry_id = ? AND examiner_id = ? LIMIT 1
                """;

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(EXIST_BY_CONTEST_ENTRY_ID_AND_EXAMINER_ID_QUERY)) {

            stmt.setInt(1, contestEntryId);
            stmt.setInt(2, examinerId);

            return stmt.executeQuery().next();
        } catch (SQLException e) {
            throw new RuntimeException("Error checking existence of contest entry examiner", e);
        }
    }
}
