package com.ntn.culinary.dao.impl;

import com.ntn.culinary.dao.NutritionDao;
import com.ntn.culinary.model.Nutrition;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.ntn.culinary.utils.DatabaseUtils.getConnection;

public class NutritionDaoImpl implements NutritionDao {

    @Override
    public Nutrition getNutritionByRecipeId(int recipeId) {

        String SELECT_NUTRITION_BY_RECIPE_ID_QUERY = """
                SELECT * FROM nutritions WHERE recipe_id = ?
                """;

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_NUTRITION_BY_RECIPE_ID_QUERY)) {

            stmt.setInt(1, recipeId);
            try (ResultSet rs = stmt.executeQuery();) {
                if (rs.next()) {
                    Nutrition nutrition = new Nutrition();
                    nutrition.setId(rs.getInt("id"));
                    nutrition.setCalories(rs.getString("calories"));
                    nutrition.setFat(rs.getString("fat"));
                    nutrition.setCholesterol(rs.getString("cholesterol"));
                    nutrition.setSodium(rs.getString("sodium"));
                    nutrition.setCarbohydrate(rs.getString("carbohydrate"));
                    nutrition.setFiber(rs.getString("fiber"));
                    nutrition.setProtein(rs.getString("protein"));
                    nutrition.setRecipeId(rs.getInt("recipe_id"));

                    return nutrition;
                } else {
                    return null; // No nutrition data found for the given recipe ID
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("SQLException: " + e.getMessage());
        }
    }

    @Override
    public void addNutrition(Nutrition nutrition) {
        String INSERT_NUTRITION_QUERY = """
                INSERT INTO nutritions (calories, fat, cholesterol, sodium, carbohydrate, fiber, protein, recipe_id)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(INSERT_NUTRITION_QUERY)) {

            stmt.setString(1, nutrition.getCalories());
            stmt.setString(2, nutrition.getFat());
            stmt.setString(3, nutrition.getCholesterol());
            stmt.setString(4, nutrition.getSodium());
            stmt.setString(5, nutrition.getCarbohydrate());
            stmt.setString(6, nutrition.getFiber());
            stmt.setString(7, nutrition.getProtein());
            stmt.setInt(8, nutrition.getRecipeId());

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error adding nutrition data", e);
        }
    }

    @Override
    public void updateNutrition(Nutrition nutrition) {
        String UPDATE_NUTRITION_QUERY = """
                UPDATE nutritions
                SET calories = ?, fat = ?, cholesterol = ?, sodium = ?, carbohydrate = ?, fiber = ?, protein = ?
                WHERE recipe_id = ? AND id = ?
                """;

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(UPDATE_NUTRITION_QUERY)) {

            stmt.setString(1, nutrition.getCalories());
            stmt.setString(2, nutrition.getFat());
            stmt.setString(3, nutrition.getCholesterol());
            stmt.setString(4, nutrition.getSodium());
            stmt.setString(5, nutrition.getCarbohydrate());
            stmt.setString(6, nutrition.getFiber());
            stmt.setString(7, nutrition.getProtein());
            stmt.setInt(8, nutrition.getRecipeId());
            stmt.setInt(9, nutrition.getId());

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error updating nutrition data", e);
        }
    }

    @Override
    public void deleteNutritionByNutritionIdRecipeId(int id, int recipeId) {
        String DELETE_NUTRITION_QUERY = """
                DELETE FROM nutritions WHERE id = ? AND recipe_id = ?
                """;

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(DELETE_NUTRITION_QUERY)) {

            stmt.setInt(1, id);
            stmt.setInt(2, recipeId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting nutrition data", e);
        }
    }

    @Override
    public boolean existsByNutritionIdAndRecipeId(int id, int recipeId) {
        String EXIST_BY_NUTRITION_ID_AND_RECIPE_ID_QUERY = """
                SELECT 1 FROM nutritions WHERE id = ? AND recipe_id = ? LIMIT 1
                """;

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(EXIST_BY_NUTRITION_ID_AND_RECIPE_ID_QUERY)) {

            stmt.setInt(1, id);
            stmt.setInt(2, recipeId);
            return stmt.executeQuery().next();
        } catch (SQLException ex) {
            throw new RuntimeException("SQLException: " + ex.getMessage());
        }
    }

    @Override
    public Nutrition getNutritionByNutritionIdAndRecipeId(int id, int recipeId) {
        String SELECT_NUTRITION_BY_NUTRITION_ID_AND_RECIPE_ID_QUERY = """
                SELECT * FROM nutritions WHERE id = ? AND recipe_id = ?
                """;

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_NUTRITION_BY_NUTRITION_ID_AND_RECIPE_ID_QUERY)) {

            stmt.setInt(1, id);
            stmt.setInt(2, recipeId);
            try (ResultSet rs = stmt.executeQuery();) {
                if (rs.next()) {
                    Nutrition nutrition = new Nutrition();
                    nutrition.setId(rs.getInt("id"));
                    nutrition.setCalories(rs.getString("calories"));
                    nutrition.setFat(rs.getString("fat"));
                    nutrition.setCholesterol(rs.getString("cholesterol"));
                    nutrition.setSodium(rs.getString("sodium"));
                    nutrition.setCarbohydrate(rs.getString("carbohydrate"));
                    nutrition.setFiber(rs.getString("fiber"));
                    nutrition.setProtein(rs.getString("protein"));
                    nutrition.setRecipeId(rs.getInt("recipe_id"));

                    return nutrition;
                } else {
                    return null; // No nutrition data found for the given IDs
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("SQLException: " + e.getMessage());
        }
    }

    @Override
    public Map<Integer, Nutrition> getNutritionByRecipeIds(List<Integer> recipeIds) {
        Map<Integer, Nutrition> result = new HashMap<>();
        if (recipeIds == null || recipeIds.isEmpty()) return result;

        StringBuilder query = new StringBuilder("SELECT * FROM nutritions WHERE recipe_id IN (");
        for (int i = 0; i < recipeIds.size(); i++) {
            query.append("?");
            if (i < recipeIds.size() - 1) query.append(", ");
        }
        query.append(")");

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query.toString())) {

            for (int i = 0; i < recipeIds.size(); i++) {
                stmt.setInt(i + 1, recipeIds.get(i));
            }

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Nutrition nutrition = new Nutrition();
                nutrition.setId(rs.getInt("id"));
                nutrition.setCalories(rs.getString("calories"));
                nutrition.setFat(rs.getString("fat"));
                nutrition.setCholesterol(rs.getString("cholesterol"));
                nutrition.setSodium(rs.getString("sodium"));
                nutrition.setCarbohydrate(rs.getString("carbohydrate"));
                nutrition.setFiber(rs.getString("fiber"));
                nutrition.setProtein(rs.getString("protein"));
                nutrition.setRecipeId(rs.getInt("recipe_id"));

                result.put(nutrition.getRecipeId(), nutrition);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error fetching nutrition by recipeIds", e);
        }

        return result;
    }
}
