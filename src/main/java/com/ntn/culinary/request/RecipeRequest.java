package com.ntn.culinary.request;

import java.util.Date;

public class RecipeRequest {
    private int id;
    private String name;
    private String category;
    private String area;
    private String instructions;
    private String image;
    private String ingredients;
    private Date publishedOn;
    private int recipedBy;
    private String prepareTime;
    private String cookingTime;
    private String yield;
    private String shortDescription;
    private String accessType;

    public RecipeRequest() {
    }

    public RecipeRequest(String name, String category, String area, String instructions, String image,
                         String ingredients, Date publishedOn, int recipedBy, String prepareTime, String cookingTime,
                         String yield, String shortDescription, String accessType) {
        this.name = name;
        this.category = category;
        this.area = area;
        this.instructions = instructions;
        this.image = image;
        this.ingredients = ingredients;
        this.publishedOn = publishedOn;
        this.recipedBy = recipedBy;
        this.prepareTime = prepareTime;
        this.cookingTime = cookingTime;
        this.yield = yield;
        this.shortDescription = shortDescription;
        this.accessType = accessType;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getIngredients() {
        return ingredients;
    }

    public void setIngredients(String ingredients) {
        this.ingredients = ingredients;
    }

    public Date getPublishedOn() {
        return publishedOn;
    }

    public void setPublishedOn(Date publishedOn) {
        this.publishedOn = publishedOn;
    }

    public int getRecipedBy() {
        return recipedBy;
    }

    public void setRecipedBy(int recipedBy) {
        this.recipedBy = recipedBy;
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

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public String getAccessType() {
        return accessType;
    }

    public void setAccessType(String accessType) {
        this.accessType = accessType;
    }

    @Override
    public String toString() {
        return "RecipeRequest{" +
               "name='" + name + '\'' +
               ", category='" + category + '\'' +
               ", area='" + area + '\'' +
               ", instructions='" + instructions + '\'' +
               ", image='" + image + '\'' +
               ", ingredients='" + ingredients + '\'' +
               ", publishedOn=" + publishedOn +
               ", recipedBy=" + recipedBy +
               ", prepareTime='" + prepareTime + '\'' +
               ", cookingTime='" + cookingTime + '\'' +
               ", yield='" + yield + '\'' +
               ", shortDescription='" + shortDescription + '\'' +
               ", accessType='" + accessType + '\'' +
               '}';
    }
}
