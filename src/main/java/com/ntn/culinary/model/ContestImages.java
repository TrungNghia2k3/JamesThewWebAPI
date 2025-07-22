package com.ntn.culinary.model;

public class ContestImages {
    private int id;
    private int contestId;
    private String imagePath;

    public ContestImages() {
    }

    public ContestImages(int contestId, String imagePath) {
        this.contestId = contestId;
        this.imagePath = imagePath;
    }

    public ContestImages(int id, int contestId, String imagePath) {
        this.id = id;
        this.contestId = contestId;
        this.imagePath = imagePath;
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

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    @Override
    public String toString() {
        return "ContestImages{" +
                "id=" + id +
                ", contestId=" + contestId +
                ", imagePath='" + imagePath + '\'' +
                '}';
    }
}
