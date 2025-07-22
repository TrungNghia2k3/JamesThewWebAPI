package com.ntn.culinary.model;


import java.sql.Date;
import java.util.List;

public class ContestEntry {
    private int id;
    private int contestId;
    private int userId;
    private String name;
    private String ingredients;
    private String instructions;
    private String image;
    private String prepareTime;
    private String cookingTime;
    private String yield;
    private String category;
    private String area;
    private String shortDescription;
    private Date dateCreated;
    private Date dateModified;
    private String status;
    private List<ContestEntryInstruction> contestEntryInstructions;

    public ContestEntry() {
    }

    public ContestEntry(int id, int contestId, int userId, String name, String ingredients, String instructions, String image, String prepareTime, String cookingTime, String yield, String category, String area, String shortDescription, Date dateCreated, Date dateModified, String status, List<ContestEntryInstruction> contestEntryInstructions) {
        this.id = id;
        this.contestId = contestId;
        this.userId = userId;
        this.name = name;
        this.ingredients = ingredients;
        this.instructions = instructions;
        this.image = image;
        this.prepareTime = prepareTime;
        this.cookingTime = cookingTime;
        this.yield = yield;
        this.category = category;
        this.area = area;
        this.shortDescription = shortDescription;
        this.dateCreated = dateCreated;
        this.dateModified = dateModified;
        this.status = status;
        this.contestEntryInstructions = contestEntryInstructions;
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

    public String getIngredients() {
        return ingredients;
    }

    public void setIngredients(String ingredients) {
        this.ingredients = ingredients;
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getPrepareTime() {
        return prepareTime;
    }

    public void setPrepareTime(String prepareTime) {
        this.prepareTime = prepareTime;
    }

    public String getCookingTime() {
        return cookingTime;
    }

    public void setCookingTime(String cookingTime) {
        this.cookingTime = cookingTime;
    }

    public String getYield() {
        return yield;
    }

    public void setYield(String yield) {
        this.yield = yield;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public Date getDateModified() {
        return dateModified;
    }

    public void setDateModified(Date dateModified) {
        this.dateModified = dateModified;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<ContestEntryInstruction> getContestEntryInstructions() {
        return contestEntryInstructions;
    }

    public void setContestEntryInstructions(List<ContestEntryInstruction> contestEntryInstructions) {
        this.contestEntryInstructions = contestEntryInstructions;
    }

    @Override
    public String toString() {
        return "ContestEntry{" +
                "id=" + id +
                ", contestId=" + contestId +
                ", userId=" + userId +
                ", name='" + name + '\'' +
                ", ingredients='" + ingredients + '\'' +
                ", instructions='" + instructions + '\'' +
                ", image='" + image + '\'' +
                ", prepareTime='" + prepareTime + '\'' +
                ", cookingTime='" + cookingTime + '\'' +
                ", yield='" + yield + '\'' +
                ", category='" + category + '\'' +
                ", area='" + area + '\'' +
                ", shortDescription='" + shortDescription + '\'' +
                ", dateCreated=" + dateCreated +
                ", dateModified=" + dateModified +
                ", status='" + status + '\'' +
                ", contestEntryInstructions=" + contestEntryInstructions +
                '}';
    }
}
