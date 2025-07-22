package com.ntn.culinary.validator;

import com.ntn.culinary.model.AnnounceWinner;
import com.ntn.culinary.request.AnnouncementRequest;
import com.ntn.culinary.utils.ValidationUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.ntn.culinary.utils.ValidationUtils.isNotExistId;
import static com.ntn.culinary.utils.ValidationUtils.isNullOrEmpty;

public class AnnouncementRequestValidator {
    public Map<String, String> validate(AnnouncementRequest request, boolean isUpdate) {
        Map<String, String> errors = new HashMap<>();

        if (isUpdate && isNotExistId(request.getContestId())) {
            errors.put("contestId", "Contest ID is required for update and must exist");
        }

        if (isNullOrEmpty(request.getTitle())) {
            errors.put("title", "Title is required");
        }

        if (isNullOrEmpty(request.getDescription())) {
            errors.put("description", "Description is required");
        }

        if (isNotExistId(request.getContestId())) {
            errors.put("contestId", "Contest ID is required and must exist");
        }

        if (request.getWinners() != null && request.getWinners().isEmpty()) {
            errors.put("winners", "Winners are required");
        } else {
            for (int i = 0; i < request.getWinners().size(); i++) {
                AnnounceWinner announceWinner = request.getWinners().get(i);

                if (isNotExistId(announceWinner.getContestEntryId())) {
                    errors.put("winners[" + i + "].contestEntryId", "Contest Entry ID is required for winner " + (i + 1));
                }

                if (isNullOrEmpty(announceWinner.getRanking())) {
                    errors.put("winners[" + i + "].ranking", "Ranking is required for winner " + (i + 1));
                }
            }
        }

        return errors;
    }
}
