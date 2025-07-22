package com.ntn.culinary.model;

public class AnnounceWinner {
    private int id;
    private int announcementId;
    private int contestEntryId;
    private String ranking;

    public AnnounceWinner() {
    }

    public AnnounceWinner(int announcementId, int contestEntryId, String ranking) {
        this.announcementId = announcementId;
        this.contestEntryId = contestEntryId;
        this.ranking = ranking;
    }

    public AnnounceWinner(int id, int announcementId, int contestEntryId, String ranking) {
        this.id = id;
        this.announcementId = announcementId;
        this.contestEntryId = contestEntryId;
        this.ranking = ranking;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getAnnouncementId() {
        return announcementId;
    }

    public void setAnnouncementId(int announcementId) {
        this.announcementId = announcementId;
    }

    public int getContestEntryId() {
        return contestEntryId;
    }

    public void setContestEntryId(int contestEntryId) {
        this.contestEntryId = contestEntryId;
    }

    public String getRanking() {
        return ranking;
    }

    public void setRanking(String ranking) {
        this.ranking = ranking;
    }

    @Override
    public String toString() {
        return "AnnounceWinner{" +
                "id=" + id +
                ", announcementId=" + announcementId +
                ", contestEntryId=" + contestEntryId +
                ", ranking='" + ranking + '\'' +
                '}';
    }
}
