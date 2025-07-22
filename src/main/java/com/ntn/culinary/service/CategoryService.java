package com.ntn.culinary.service;

import com.ntn.culinary.request.CategoryRequest;
import com.ntn.culinary.response.CategoryResponse;

import java.util.List;

public interface CategoryService {
    void addCategory(CategoryRequest categoryRequest);

    void updateCategory(CategoryRequest categoryRequest);

    CategoryResponse getCategoryById(int id);

    List<CategoryResponse> getAllCategories();

    void deleteCategory(int id);

    CategoryResponse getCategoryByName(String name);
}
