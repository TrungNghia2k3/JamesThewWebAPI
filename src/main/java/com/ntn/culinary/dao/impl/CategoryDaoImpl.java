package com.ntn.culinary.dao.impl;

import com.ntn.culinary.dao.CategoryDao;
import com.ntn.culinary.model.Category;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static com.ntn.culinary.utils.DatabaseUtils.getConnection;

public class CategoryDaoImpl implements CategoryDao {

    @Override
    public boolean existsByName(String name) {

        String CHECK_CATEGORY_EXISTS_BY_NAME_QUERY = "SELECT 1 FROM categories WHERE name = ? LIMIT 1";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(CHECK_CATEGORY_EXISTS_BY_NAME_QUERY)) {

            stmt.setString(1, name);
            return stmt.executeQuery().next();
        } catch (SQLException e) {
            throw new RuntimeException("SQLException: " + e.getMessage());
        }
    }

    @Override
    public void insertCategory(Category category) {
        String INSERT_CATEGORY_QUERY = "INSERT INTO categories (name, path) VALUES (?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(INSERT_CATEGORY_QUERY)) {

            stmt.setString(1, category.getName());
            stmt.setString(2, category.getPath());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("SQLException: " + e.getMessage());
        }
    }

    @Override
    public void updateCategory(Category category) {
        String UPDATE_CATEGORY_QUERY = "UPDATE categories SET name = ?, path = ? WHERE id = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(UPDATE_CATEGORY_QUERY)) {

            stmt.setString(1, category.getName());
            stmt.setString(2, category.getPath());
            stmt.setInt(3, category.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("SQLException: " + e.getMessage());
        }
    }

    @Override
    public List<Category> getAllCategories() {

        String SELECT_ALL_CATEGORIES_QUERY = "SELECT * FROM categories";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_ALL_CATEGORIES_QUERY)) {

            List<Category> categories = new ArrayList<>();

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Category category = new Category();
                    category.setId(rs.getInt("id"));
                    category.setName(rs.getString("name"));
                    category.setPath(rs.getString("path"));
                    categories.add(category);
                }
                return categories;
            }
        } catch (SQLException e) {
            throw new RuntimeException("SQLException: " + e.getMessage());
        }
    }

    @Override
    public Category getCategoryById(int id) {

        String SELECT_CATEGORY_BY_ID_QUERY = "SELECT * FROM categories WHERE id = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_CATEGORY_BY_ID_QUERY)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery();) {
                if (rs.next()) {
                    Category category = new Category();
                    category.setId(rs.getInt("id"));
                    category.setName(rs.getString("name"));
                    category.setPath(rs.getString("path"));
                    return category;
                } else {
                    return null;
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("SQLException: " + e.getMessage());
        }
    }

    @Override
    public void deleteCategoryById(int id) {
        String DELETE_CATEGORY_BY_ID_QUERY = "DELETE FROM categories WHERE id = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(DELETE_CATEGORY_BY_ID_QUERY)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("SQLException: " + e.getMessage());
        }
    }

    @Override
    public boolean existsById(int id) {
        String CHECK_CATEGORY_EXISTS_BY_ID_QUERY = "SELECT 1 FROM categories WHERE id = ? LIMIT 1";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(CHECK_CATEGORY_EXISTS_BY_ID_QUERY)) {

            stmt.setInt(1, id);
            return stmt.executeQuery().next();
        } catch (SQLException e) {
            throw new RuntimeException("SQLException: " + e.getMessage());
        }
    }

    @Override
    public Category getCategoryByName(String name) {
        String SELECT_CATEGORY_BY_NAME_QUERY = "SELECT * FROM categories WHERE name = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_CATEGORY_BY_NAME_QUERY)) {

            stmt.setString(1, name.toUpperCase());
            try (ResultSet rs = stmt.executeQuery();) {
                if (rs.next()) {
                    Category category = new Category();
                    category.setId(rs.getInt("id"));
                    category.setName(rs.getString("name"));
                    category.setPath(rs.getString("path"));
                    return category;
                } else {
                    return null;
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("SQLException: " + e.getMessage());
        }
    }

    @Override
    public boolean existsCategoryWithNameExcludingId(int id, String name) {
        String CHECK_CATEGORY_EXISTS_EXCLUDING_ID_QUERY = "SELECT 1 FROM categories WHERE id != ? AND name = ? LIMIT 1";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(CHECK_CATEGORY_EXISTS_EXCLUDING_ID_QUERY)) {

            stmt.setInt(1, id);
            stmt.setString(2, name);
            return stmt.executeQuery().next();
        } catch (SQLException e) {
            throw new RuntimeException("SQLException: " + e.getMessage());
        }
    }
}


