package com.ntn.culinary.service.impl;

import com.ntn.culinary.dao.CategoryDao;
import com.ntn.culinary.exception.ConflictException;
import com.ntn.culinary.exception.NotFoundException;
import com.ntn.culinary.model.Category;
import com.ntn.culinary.request.CategoryRequest;
import com.ntn.culinary.response.CategoryResponse;
import com.ntn.culinary.service.CategoryService;

import java.util.List;

import static com.ntn.culinary.constant.Cloudinary.CLOUDINARY_URL;
import static com.ntn.culinary.utils.ImageUtils.*;
import static com.ntn.culinary.utils.StringUtils.slugify;
import static java.util.stream.Collectors.toList;

public class CategoryServiceImpl implements CategoryService {
    private final CategoryDao categoryDao;

    public CategoryServiceImpl(CategoryDao categoryDao) {
        this.categoryDao = categoryDao;
    }

    @Override
    public void addCategory(CategoryRequest categoryRequest) {
        // Validate the request
        validateCategoryRequest(categoryRequest, false);

        String fileName = null;
        // Check if image is provided and has size greater than 0
        if (categoryRequest.getImage() != null && categoryRequest.getImage().getSize() > 0) {
            // Tạo slug từ tên danh mục
            String slug = slugify(categoryRequest.getName());

            // Lưu ảnh và lấy tên file
            fileName = saveImage(categoryRequest.getImage(), slug, "categories");
        }

        // Create new category object
        Category newCategory = new Category();
        newCategory.setName(categoryRequest.getName());
        newCategory.setPath(fileName);

        categoryDao.insertCategory(newCategory);
    }

    @Override
    public void updateCategory(CategoryRequest categoryRequest) {
        // Validate the request
        validateCategoryRequest(categoryRequest, true);

        Category existingCategory = categoryDao.getCategoryById(categoryRequest.getId());
        if (existingCategory == null) {
            throw new NotFoundException("Category not found");
        }

        String fileName = null;
        if (categoryRequest.getImage() != null && categoryRequest.getImage().getSize() > 0) {
            // Xóa ảnh cũ nếu có
            if (existingCategory.getPath() != null) {
                deleteImage(existingCategory.getPath(), "categories");
            }

            // Tạo slug từ tên danh mục
            String slug = slugify(categoryRequest.getName());

            // Lưu ảnh và lấy tên file
            fileName = saveImage(categoryRequest.getImage(), slug, "categories");
        }

        // Create updated category object
        Category updatedCategory = new Category();
        updatedCategory.setId(categoryRequest.getId());
        updatedCategory.setName(categoryRequest.getName());
        updatedCategory.setPath(fileName);

        categoryDao.updateCategory(updatedCategory);
    }

    @Override
    public CategoryResponse getCategoryById(int id) {
        Category category = categoryDao.getCategoryById(id);

        if (category == null) {
            throw new NotFoundException("Category with id " + id + " not found");
        }

        return mapCategoryToResponse(category);
    }

    @Override
    public List<CategoryResponse> getAllCategories() {
        return categoryDao.getAllCategories().stream()
                .map(this::mapCategoryToResponse)
                .collect(toList());
    }

    @Override
    public void deleteCategory(int id) {
        if (!categoryDao.existsById(id)) {
            throw new NotFoundException("Category with id not found");
        }

        Category category = categoryDao.getCategoryById(id);
        if (category == null) {
            throw new NotFoundException("Category not found");
        }

        // Xóa ảnh nếu có
        if (category.getPath() != null) {
            deleteImage(category.getPath(), "categories");
        }

        categoryDao.deleteCategoryById(id);
    }

    @Override
    public CategoryResponse getCategoryByName(String name) {
        Category category = categoryDao.getCategoryByName(name);

        if (category == null) {
            throw new NotFoundException("Category with name " + name + " not found");
        }

        return mapCategoryToResponse(category);
    }

    private CategoryResponse mapCategoryToResponse(Category category) {
        String imageUrl = CLOUDINARY_URL + "categories/" + category.getPath();
        return new CategoryResponse(
                category.getId(),
                category.getName(),
                imageUrl
        );
    }

    private void validateCategoryRequest(CategoryRequest request, boolean isUpdate) {
        // CREATE
        if (!isUpdate && categoryDao.existsByName(request.getName())) {
            throw new ConflictException("Category with name already exists");
        }

        // UPDATE
        if (isUpdate && categoryDao.existsById(request.getId())) {
            throw new NotFoundException("Category with id not found");
        }

        if (isUpdate && categoryDao.existsCategoryWithNameExcludingId(request.getId(), request.getName())) {
            throw new ConflictException("Category with name already exists, excluding the current one");
        }
    }
}
