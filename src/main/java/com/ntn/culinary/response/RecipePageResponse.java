package com.ntn.culinary.response;

import java.util.List;

public class RecipePageResponse {
    private List<RecipeResponse> recipes;
    private int currentPage;
    private int totalPages;
    private int totalItems;

    public RecipePageResponse(List<RecipeResponse> recipes, int totalItems, int currentPage, int totalPages) {
        this.recipes = recipes;
        this.currentPage = currentPage;
        this.totalPages = totalPages;
        this.totalItems = totalItems;
    }
}
