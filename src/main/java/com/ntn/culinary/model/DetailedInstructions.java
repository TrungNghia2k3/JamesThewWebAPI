package com.ntn.culinary.model;

public class DetailedInstructions {
    private int id;
    private String name;
    private String text;
    private String image;
    private int recipeId;

    public DetailedInstructions() {
    }

    public DetailedInstructions(int id, String name, String text, String image, int recipeId) {
        this.id = id;
        this.name = name;
        this.text = text;
        this.image = image;
        this.recipeId = recipeId;
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

    public int getRecipeId() {
        return recipeId;
    }

    public void setRecipeId(int recipeId) {
        this.recipeId = recipeId;
    }
}
