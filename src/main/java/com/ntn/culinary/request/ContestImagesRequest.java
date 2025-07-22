package com.ntn.culinary.request;

import javax.servlet.http.Part;

public class ContestImagesRequest {
    private int id;
    private int contestId;
    private Part image;

    public ContestImagesRequest() {
    }

    public ContestImagesRequest(int id, int contestId, Part image) {
        this.id = id;
        this.contestId = contestId;
        this.image = image;
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

    public Part getImage() {
        return image;
    }

    public void setImage(Part image) {
        this.image = image;
    }
}
