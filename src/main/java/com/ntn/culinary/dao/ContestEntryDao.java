package com.ntn.culinary.dao;

import com.ntn.culinary.model.ContestEntry;

import java.util.List;

public interface ContestEntryDao {
    void addContestEntry(ContestEntry contestEntry);

    void updateContestEntry(ContestEntry contestEntry, String imageFileName);

    int getContestEntryIdByUserIdAndContestId(int userId, int contestId);

    int getContestEntryIdByUserIdAndContestIdAndName(int userId, int contestId, String name);

    boolean existsByUserIdAndContestIdAndName(int userId, int contestId, String name);

    ContestEntry getContestEntryByUserIdAndContestIdAndName(int userId, int contestId, String name);

    void updateContestEntryStatus(int contestEntryId, String status);

    boolean existsById(int contestEntryId);

    ContestEntry getContestEntryById(int contestEntryId);

    void deleteContestEntryByUserIdAndContestIdAndName(int userId, int contestId, String name);

    List<ContestEntry> getContestEntryByContestId(int contestId);

    List<ContestEntry> getContestEntriesByUserId(int userId);

    ContestEntry getContestEntryByUserIdAndContestId(int userId, int contestId);

    boolean existsByNameAndContestId(String name, int contestId);

    boolean existsContestEntryWithNameExcludingId(String name, int contestId, int contestEntryId);
}
