package com.ntn.culinary.service;

import com.ntn.culinary.request.ContestRequest;
import com.ntn.culinary.response.ContestResponse;

import java.util.List;

public interface ContestService {
    List<ContestResponse> getAllContests();

    ContestResponse getContestById(int id);

    void addContest(ContestRequest contestRequest);

    void updateContest(ContestRequest contestRequest);

    void updateContestStatus(int id);

    void deleteContest(int id);
}
