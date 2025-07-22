package com.ntn.culinary.request;

public class DeleteContestEntryRequest {
    private int id;
    private int contestId;
    private int userId;
    private String name;

    public DeleteContestEntryRequest() {
    }

    public DeleteContestEntryRequest(int id, int contestId, int userId, String name) {
        this.id = id;
        this.contestId = contestId;
        this.userId = userId;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getContestId() {
        return contestId;
    }

    public void setContestId(int contestId) {
        this.contestId = contestId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
