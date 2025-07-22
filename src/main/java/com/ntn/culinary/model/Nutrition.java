package com.ntn.culinary.model;

public class Nutrition {
    private int id;
    private String calories;
    private String fat;
    private String cholesterol;
    private String sodium;
    private String carbohydrate;
    private String fiber;
    private String protein;
    private int recipeId;

    public Nutrition() {
    }

    public Nutrition(int id, String calories, String fat, String cholesterol, String sodium, String carbohydrate, String fiber, String protein, int recipeId) {
        this.id = id;
        this.calories = calories;
        this.fat = fat;
        this.cholesterol = cholesterol;
        this.sodium = sodium;
        this.carbohydrate = carbohydrate;
        this.fiber = fiber;
        this.protein = protein;
        this.recipeId = recipeId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCalories() {
        return calories;
    }

    public void setCalories(String calories) {
        this.calories = calories;
    }

    public String getFat() {
        return fat;
    }

    public void setFat(String fat) {
        this.fat = fat;
    }

    public String getCholesterol() {
        return cholesterol;
    }

    public void setCholesterol(String cholesterol) {
        this.cholesterol = cholesterol;
    }

    public String getSodium() {
        return sodium;
    }

    public void setSodium(String sodium) {
        this.sodium = sodium;
    }

    public String getCarbohydrate() {
        return carbohydrate;
    }

    public void setCarbohydrate(String carbohydrate) {
        this.carbohydrate = carbohydrate;
    }

    public String getFiber() {
        return fiber;
    }

    public void setFiber(String fiber) {
        this.fiber = fiber;
    }

    public String getProtein() {
        return protein;
    }

    public void setProtein(String protein) {
        this.protein = protein;
    }

    public int getRecipeId() {
        return recipeId;
    }

    public void setRecipeId(int recipeId) {
        this.recipeId = recipeId;
    }

    @Override
    public String toString() {
        return "Nutrition{" +
                "id=" + id +
                ", calories='" + calories + '\'' +
                ", fat='" + fat + '\'' +
                ", cholesterol='" + cholesterol + '\'' +
                ", sodium='" + sodium + '\'' +
                ", carbohydrates='" + carbohydrate + '\'' +
                ", fiber='" + fiber + '\'' +
                ", protein='" + protein + '\'' +
                ", recipeId=" + recipeId +
                '}';
    }
}
