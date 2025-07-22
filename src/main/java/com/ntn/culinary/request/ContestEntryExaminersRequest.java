package com.ntn.culinary.request;

import java.sql.Timestamp;

public class ContestEntryExaminersRequest {
    private int id;
    private int contestEntryId;
    private int examinerId;
    private int score;
    private String feedback;
    private Timestamp examDate; // Date + Time

    public ContestEntryExaminersRequest() {
    }

    public ContestEntryExaminersRequest(int contestEntryId, int examinerId, int score, String feedback, Timestamp examDate) {
        this.contestEntryId = contestEntryId;
        this.examinerId = examinerId;
        this.score = score;
        this.feedback = feedback;
        this.examDate = examDate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getContestEntryId() {
        return contestEntryId;
    }

    public void setContestEntryId(int contestEntryId) {
        this.contestEntryId = contestEntryId;
    }

    public int getExaminerId() {
        return examinerId;
    }

    public void setExaminerId(int examinerId) {
        this.examinerId = examinerId;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public Timestamp getExamDate() {
        return examDate;
    }

    public void setExamDate(Timestamp examDate) {
        this.examDate = examDate;
    }

    @Override
    public String toString() {
        return "ContestEntryExaminers{" +
                "id=" + id +
                ", contestEntryId=" + contestEntryId +
                ", examinerId=" + examinerId +
                ", score=" + score +
                ", feedback='" + feedback + '\'' +
                ", examDate=" + examDate +
                '}';
    }
}
