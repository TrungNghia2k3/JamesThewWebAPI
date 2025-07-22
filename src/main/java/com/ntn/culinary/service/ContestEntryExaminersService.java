package com.ntn.culinary.service;

import com.ntn.culinary.model.ContestEntryExaminers;
import com.ntn.culinary.request.ContestEntryExaminersRequest;

import java.util.List;

public interface ContestEntryExaminersService {
    void addExaminer(ContestEntryExaminersRequest request);

    void updateExaminer(ContestEntryExaminersRequest request);

    void deleteExaminer(int contestEntryId, int examinerId);

    ContestEntryExaminers getContestEntryExaminerById(int id);

    List<ContestEntryExaminers> getContestEntryExaminersByContestEntryId(int contestEntryId);

    List<ContestEntryExaminers> getContestEntryExaminersByExaminerId(int examinerId);
}
