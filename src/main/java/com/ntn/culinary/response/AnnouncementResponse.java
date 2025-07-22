package com.ntn.culinary.response;

import com.ntn.culinary.model.Contest;

import java.sql.Date;
import java.util.List;

public class AnnouncementResponse {
    private int id;
    private String title;
    private Date announcementDate;
    private String description;
    private Contest contest;
    private List<AnnounceWinnerResponse> winners;

    public AnnouncementResponse() {
    }

    public AnnouncementResponse(int id, String title, Date announcementDate, String description, Contest contest, List<AnnounceWinnerResponse> winners) {
        this.id = id;
        this.title = title;
        this.announcementDate = announcementDate;
        this.description = description;
        this.contest = contest;
        this.winners = winners;
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

    public Contest getContest() {
        return contest;
    }

    public void setContest(Contest contest) {
        this.contest = contest;
    }

    public List<AnnounceWinnerResponse> getWinners() {
        return winners;
    }

    public void setWinners(List<AnnounceWinnerResponse> winners) {
        this.winners = winners;
    }
}
