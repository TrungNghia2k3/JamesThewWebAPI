package com.ntn.culinary.service;

import com.ntn.culinary.request.ContestEntryRequest;
import com.ntn.culinary.request.DeleteContestEntryRequest;
import com.ntn.culinary.response.ContestEntryResponse;

import javax.servlet.http.Part;
import java.util.List;

public interface ContestEntryService {
    void addContestEntry(ContestEntryRequest contestEntryRequest, Part imagePart);

    void updateContestEntry(ContestEntryRequest contestEntryRequest, Part imagePart);

    void deleteContestEntry(DeleteContestEntryRequest request);

    ContestEntryResponse getContestEntryByUserIdAndContestId(int userId, int contestId);

    ContestEntryResponse getContestEntryById(int id);

    List<ContestEntryResponse> getContestEntriesByContestId(int contestId);

    List<ContestEntryResponse> getContestEntriesByUserId(int userId);
}
