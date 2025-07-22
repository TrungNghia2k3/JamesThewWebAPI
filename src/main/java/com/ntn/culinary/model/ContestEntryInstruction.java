package com.ntn.culinary.model;

public class ContestEntryInstruction {
    private int id;
    private int contestEntryId;
    private int stepNumber;
    private String name;
    private String text;
    private String image;

    public ContestEntryInstruction() {
    }

    public ContestEntryInstruction(int contestEntryId, int stepNumber, String name, String text, String image) {
        this.contestEntryId = contestEntryId;
        this.stepNumber = stepNumber;
        this.name = name;
        this.text = text;
        this.image = image;
    }

    public ContestEntryInstruction(int id, int contestEntryId, int stepNumber, String name, String text, String image) {
        this.id = id;
        this.contestEntryId = contestEntryId;
        this.stepNumber = stepNumber;
        this.name = name;
        this.text = text;
        this.image = image;
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

    public int getStepNumber() {
        return stepNumber;
    }

    public void setStepNumber(int stepNumber) {
        this.stepNumber = stepNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    @Override
    public String toString() {
        return "ContestEntryInstructions{" +
                "id=" + id +
                ", contestEntryId=" + contestEntryId +
                ", stepNumber=" + stepNumber +
                ", name='" + name + '\'' +
                ", text='" + text + '\'' +
                ", image='" + image + '\'' +
                '}';
    }
}
