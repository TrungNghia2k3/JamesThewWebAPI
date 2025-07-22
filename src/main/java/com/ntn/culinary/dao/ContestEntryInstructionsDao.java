package com.ntn.culinary.dao;

import com.ntn.culinary.model.ContestEntryInstruction;

import java.util.List;

public interface ContestEntryInstructionsDao {
    void addContestEntryInstructions(ContestEntryInstruction contestEntryInstruction);

    boolean existsByContestEntryIdAndInstructionId(int contestEntryId, int instructionId);

    void deleteContestEntryInstructionsByContestEntryIdAndInstructionId(int contestEntryId, int instructionId);

    List<ContestEntryInstruction> getContestEntryInstructionsByContestEntryId(int contestEntryId);

    void updateContestEntryInstructions(ContestEntryInstruction contestEntryInstruction);

    void deleteContestEntryInstructionById(int id);
}
