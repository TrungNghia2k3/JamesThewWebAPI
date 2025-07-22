package com.ntn.culinary.request;

import com.ntn.culinary.model.AnnounceWinner;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

public class AnnouncementRequest {
    private int id;
    private String title;
    private Date announcementDate;
    private String description;
    private int contestId;
    private List<AnnounceWinner> winners = new ArrayList<>();

    public AnnouncementRequest() {
        // winners already initialized
    }

    public AnnouncementRequest(int id, String title, Date announcementDate, String description, int contestId, List<AnnounceWinner> winners) {
        this.id = id;
        this.title = title;
        this.announcementDate = announcementDate;
        this.description = description;
        this.contestId = contestId;
        this.winners = (winners != null) ? winners : new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getAnnouncementDate() {
        return announcementDate;
    }

    public void setAnnouncementDate(Date announcementDate) {
        this.announcementDate = announcementDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getContestId() {
        return contestId;
    }

    public void setContestId(int contestId) {
        this.contestId = contestId;
    }

    public List<AnnounceWinner> getWinners() {
        return winners;
    }

    public void setWinners(List<AnnounceWinner> winners) {
        this.winners = (winners != null) ? winners : new ArrayList<>();
    }
}
