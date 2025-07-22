package com.ntn.culinary.dao;

import com.ntn.culinary.model.Category;

import java.util.List;

public interface CategoryDao {
    /**
     * Checks if a category exists by its name.
     *
     * @param name the name of the category
     * @return true if the category exists, false otherwise
     */
    boolean existsByName(String name);

    /**
     * Inserts a new category into the data source.
     *
     * @param category the category to insert
     */
    void insertCategory(Category category);

    /**
     * Updates an existing category in the data source.
     *
     * @param category the category with updated information
     */
    void updateCategory(Category category);

    /**
     * Retrieves all categories from the data source.
     *
     * @return a list of all categories
     */
    List<Category> getAllCategories();

    /**
     * Retrieves a category by its unique identifier.
     *
     * @param id the id of the category
     * @return the category with the specified id, or null if not found
     */
    Category getCategoryById(int id);

    /**
     * Deletes a category by its unique identifier.
     *
     * @param id the id of the category to delete
     */
    void deleteCategoryById(int id);

    /**
     * Checks if a category exists by its unique identifier.
     *
     * @param id the id of the category
     * @return true if the category exists, false otherwise
     */
    boolean existsById(int id);

    /**
     * Retrieves a category by its name.
     *
     * @param name the name of the category
     * @return the category with the specified name, or null if not found
     */
    Category getCategoryByName(String name);

    boolean existsCategoryWithNameExcludingId(int id, String name);
}
