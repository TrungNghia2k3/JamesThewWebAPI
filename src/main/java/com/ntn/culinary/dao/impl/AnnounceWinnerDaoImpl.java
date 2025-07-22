package com.ntn.culinary.dao.impl;

import com.ntn.culinary.dao.AnnounceWinnerDao;
import com.ntn.culinary.model.AnnounceWinner;
import com.ntn.culinary.utils.DatabaseUtils;

import static com.ntn.culinary.utils.DatabaseUtils.getConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AnnounceWinnerDaoImpl implements AnnounceWinnerDao {
    @Override
    public void insertWinner(AnnounceWinner announceWinner) {

        String INSERT_WINNER_QUERY = "INSERT INTO announce_winners (announcement_id, contest_entry_id, ranking) VALUES (?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(INSERT_WINNER_QUERY)) {
            stmt.setInt(1, announceWinner.getAnnouncementId());
            stmt.setInt(2, announceWinner.getContestEntryId());
            stmt.setString(3, announceWinner.getRanking());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error inserting winner: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean existsWinner(int announcementId, int contestEntryId) {

        String CHECK_WINNER_EXISTS_QUERY = "SELECT 1 FROM announce_winners WHERE announcement_id = ? AND contest_entry_id = ? LIMIT 1";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(CHECK_WINNER_EXISTS_QUERY)) {
            stmt.setInt(1, announcementId);
            stmt.setInt(2, contestEntryId);
            return stmt.executeQuery().next();
        } catch (SQLException e) {
            throw new RuntimeException("Error checking winner existence: " + e.getMessage(), e);
        }
    }

    @Override
    public void updateWinner(AnnounceWinner announceWinner) {

        String UPDATE_WINNER_QUERY_BY_ID_QUERY = "UPDATE announce_winners SET ranking = ?, contest_entry_id = ? WHERE id = ? AND announcement_id = ?";

        try (Connection conn = DatabaseUtils.getConnection();
             PreparedStatement stmt = conn.prepareStatement(UPDATE_WINNER_QUERY_BY_ID_QUERY)) {
            stmt.setString(1, announceWinner.getRanking());
            stmt.setInt(2, announceWinner.getContestEntryId());
            stmt.setInt(3, announceWinner.getId());
            stmt.setInt(4, announceWinner.getAnnouncementId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error updating winner: " + e.getMessage(), e);
        }
    }

    @Override
    public void deleteWinner(int announcementId, int contestEntryId) {

        String DELETE_WINNER_QUERY_BY_ANNOUNCEMENT_ID_AND_ENTRY_ID = "DELETE FROM announce_winners WHERE announcement_id = ? AND contest_entry_id = ?";

        try (Connection conn = DatabaseUtils.getConnection();
             PreparedStatement stmt = conn.prepareStatement(DELETE_WINNER_QUERY_BY_ANNOUNCEMENT_ID_AND_ENTRY_ID)) {
            stmt.setInt(1, announcementId);
            stmt.setInt(2, contestEntryId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting winner: " + e.getMessage(), e);
        }
    }

    @Override
    public List<AnnounceWinner> getAllWinnersByAnnouncementId(int announcementId) {

        String SELECT_ALL_WINNERS_BY_ANNOUNCEMENT_ID_QUERY = "SELECT * FROM announce_winners WHERE announcement_id = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_ALL_WINNERS_BY_ANNOUNCEMENT_ID_QUERY)) {
            stmt.setInt(1, announcementId);

            List<AnnounceWinner> winners = new ArrayList<>();
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    AnnounceWinner winner = new AnnounceWinner();
                    winner.setId(rs.getInt("id"));
                    winner.setContestEntryId(rs.getInt("contest_entry_id"));
                    winner.setRanking(rs.getString("ranking"));
                    winners.add(winner);
                }
                return winners;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving winners by announcement ID: " + e.getMessage(), e);
        }
    }
}
