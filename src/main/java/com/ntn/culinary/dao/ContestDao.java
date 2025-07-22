package com.ntn.culinary.dao;

import com.ntn.culinary.model.Contest;

import java.util.List;

public interface ContestDao {
    List<Contest> getAllContests();

    Contest getContestById(int id);

    boolean existsById(int id);

    void addContest(Contest contest);

    void updateContest(Contest contest);

    void updateContestStatus(int id, boolean isClosed);

    void deleteContestById(int id);

    int getContestIdByHeadline(String headline);

    boolean existsByHeadline(String headline);

    boolean isContestClosed(int id);
}
