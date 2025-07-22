package com.ntn.culinary.response;

import java.util.Date;

public class CommentResponse {
    private int id;
    private int userId;
    private int recipeId;
    private String content;
    private Date date;
    private int rating;
    private boolean isBanned;

    public CommentResponse() {
    }

    public CommentResponse(int id, int userId, int recipeId, String content, Date date, int rating, boolean isBanned) {
        this.id = id;
        this.userId = userId;
        this.recipeId = recipeId;
        this.content = content;
        this.date = date;
        this.rating = rating;
        this.isBanned = isBanned;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getRecipeId() {
        return recipeId;
    }

    public void setRecipeId(int recipeId) {
        this.recipeId = recipeId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public boolean isBanned() {
        return isBanned;
    }

    public void setBanned(boolean banned) {
        isBanned = banned;
    }
}
