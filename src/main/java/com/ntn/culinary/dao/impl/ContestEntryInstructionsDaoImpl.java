package com.ntn.culinary.dao.impl;

import com.ntn.culinary.dao.ContestEntryInstructionsDao;
import com.ntn.culinary.model.ContestEntryInstruction;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static com.ntn.culinary.utils.DatabaseUtils.getConnection;

public class ContestEntryInstructionsDaoImpl implements ContestEntryInstructionsDao {

    @Override
    public void addContestEntryInstructions(ContestEntryInstruction contestEntryInstruction) {

        String INSERT_CONTEST_ENTRY_INSTRUCTIONS_QUERY = """
                INSERT INTO contest_entry_instructions (contest_entry_id, step_number, name, text, image)
                VALUES (?, ?, ?, ?, ?)
                """;

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(INSERT_CONTEST_ENTRY_INSTRUCTIONS_QUERY)) {

            stmt.setInt(1, contestEntryInstruction.getContestEntryId());
            stmt.setInt(2, contestEntryInstruction.getStepNumber());
            stmt.setString(3, contestEntryInstruction.getName());
            stmt.setString(4, contestEntryInstruction.getText());
            stmt.setString(5, contestEntryInstruction.getImage());

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error adding contest entry instructions", e);
        }
    }

    @Override
    public boolean existsByContestEntryIdAndInstructionId(int contestEntryId, int instructionId) {
        String CHECK_EXISTENCE_QUERY = """
                SELECT 1 FROM contest_entry_instructions
                WHERE contest_entry_id = ? AND id = ? LIMIT 1
                """;

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(CHECK_EXISTENCE_QUERY)) {

            stmt.setInt(1, contestEntryId);
            stmt.setInt(2, instructionId);

            return stmt.executeQuery().next() && stmt.executeQuery().getInt(1) > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error checking existence of contest entry instructions", e);
        }
    }

    @Override
    public void deleteContestEntryInstructionsByContestEntryIdAndInstructionId(int contestEntryId, int instructionId) {
        String DELETE_CONTEST_ENTRY_INSTRUCTIONS_QUERY = """
                DELETE FROM contest_entry_instructions
                WHERE contest_entry_id = ? AND id = ?
                """;

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(DELETE_CONTEST_ENTRY_INSTRUCTIONS_QUERY)) {

            stmt.setInt(1, contestEntryId);
            stmt.setInt(2, instructionId);

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting contest entry instructions", e);
        }
    }

    @Override
    public List<ContestEntryInstruction> getContestEntryInstructionsByContestEntryId(int contestEntryId) {
        String GET_CONTEST_ENTRY_INSTRUCTIONS_QUERY = """
                SELECT * FROM contest_entry_instructions
                WHERE contest_entry_id = ?
                """;

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(GET_CONTEST_ENTRY_INSTRUCTIONS_QUERY)) {

            stmt.setInt(1, contestEntryId);
            var resultSet = stmt.executeQuery();

            List<ContestEntryInstruction> instructionsList = new ArrayList<>();
            while (resultSet.next()) {
                ContestEntryInstruction instruction = new ContestEntryInstruction(
                        resultSet.getInt("id"),
                        resultSet.getInt("contest_entry_id"),
                        resultSet.getInt("step_number"),
                        resultSet.getString("name"),
                        resultSet.getString("text"),
                        resultSet.getString("image")
                );
                instructionsList.add(instruction);
            }
            return instructionsList;
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving contest entry instructions", e);
        }
    }

    @Override
    public void updateContestEntryInstructions(ContestEntryInstruction contestEntryInstruction) {
        String UPDATE_CONTEST_ENTRY_INSTRUCTIONS_QUERY = """
                UPDATE contest_entry_instructions
                SET step_number = ?, name = ?, text = ?, image = ?
                WHERE contest_entry_id = ? AND id = ?
                """;

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(UPDATE_CONTEST_ENTRY_INSTRUCTIONS_QUERY)) {

            stmt.setInt(1, contestEntryInstruction.getStepNumber());
            stmt.setString(2, contestEntryInstruction.getName());
            stmt.setString(3, contestEntryInstruction.getText());
            stmt.setString(4, contestEntryInstruction.getImage());
            stmt.setInt(5, contestEntryInstruction.getContestEntryId());
            stmt.setInt(6, contestEntryInstruction.getId());

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error updating contest entry instructions", e);
        }
    }

    @Override
    public void deleteContestEntryInstructionById(int id) {
        String DELETE_CONTEST_ENTRY_INSTRUCTION_QUERY = """
                DELETE FROM contest_entry_instructions
                WHERE id = ?
                """;

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(DELETE_CONTEST_ENTRY_INSTRUCTION_QUERY)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting contest entry instruction", e);
        }
    }
}
