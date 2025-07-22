package com.ntn.culinary.model;

import java.sql.Date;
import java.util.List;

public class Announcement {
    private int id;
    private String title;
    private Date announcementDate;
    private String description;
    private int contestId;
    private List<AnnounceWinner> winners;

    public Announcement() {
    }

    public Announcement(String title, Date announcementDate, String description, int contestId, List<AnnounceWinner> winners) {
        this.title = title;
        this.announcementDate = announcementDate;
        this.description = description;
        this.contestId = contestId;
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
        this.winners = winners;
    }

    @Override
    public String toString() {
        return "Announcement{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", announcementDate=" + announcementDate +
                ", description='" + description + '\'' +
                ", contestId=" + contestId +
                ", winners=" + winners +
                '}';
    }
}
