package com.ntn.culinary.response;

import com.ntn.culinary.model.ContestEntry;

public class AnnounceWinnerResponse {
    private int id;
    private ContestEntry contestEntry;
    private String ranking;

    public AnnounceWinnerResponse() {
    }

    public AnnounceWinnerResponse(int id, ContestEntry contestEntry, String ranking) {
        this.id = id;
        this.contestEntry = contestEntry;
        this.ranking = ranking;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ContestEntry getContestEntry() {
        return contestEntry;
    }

    public void setContestEntry(ContestEntry contestEntry) {
        this.contestEntry = contestEntry;
    }

    public String getRanking() {
        return ranking;
    }

    public void setRanking(String ranking) {
        this.ranking = ranking;
    }
}
