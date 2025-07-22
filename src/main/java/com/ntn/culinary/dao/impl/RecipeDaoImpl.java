package com.ntn.culinary.dao.impl;

import com.ntn.culinary.dao.RecipeDao;
import com.ntn.culinary.model.Recipe;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static com.ntn.culinary.utils.DatabaseUtils.getConnection;

public class RecipeDaoImpl implements RecipeDao {

    @Override
    public void addRecipe(Recipe recipe) {
        String INSERT_RECIPE_QUERY = """
                INSERT INTO recipes (
                    name, category, area, instructions, image, ingredients, 
                    published_on, reciped_by, prepare_time, cooking_time, yield, 
                    short_description, access_type
                )
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(INSERT_RECIPE_QUERY)) {

            stmt.setString(1, recipe.getName());
            stmt.setString(2, recipe.getCategory());
            stmt.setString(3, recipe.getArea());
            stmt.setString(4, recipe.getInstructions());
            stmt.setString(5, recipe.getImage());
            stmt.setString(6, recipe.getIngredients());
            stmt.setDate(7, recipe.getPublishedOn());
            stmt.setInt(8, recipe.getRecipedBy());
            stmt.setString(9, recipe.getPrepareTime());
            stmt.setString(10, recipe.getCookingTime());
            stmt.setString(11, recipe.getYield());
            stmt.setString(12, recipe.getShortDescription());
            stmt.setString(13, recipe.getAccessType());

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error adding recipe: " + e.getMessage(), e);
        }
    }

    @Override
    public void updateRecipe(Recipe recipe) {
        String UPDATE_RECIPE_QUERY = """
                UPDATE recipes SET 
                    name = ?, category = ?, area = ?, instructions = ?, 
                    image = ?, ingredients = ?, published_on = ?, 
                    reciped_by = ?, prepare_time = ?, cooking_time = ?, 
                    yield = ?, short_description = ?, access_type = ?
                WHERE id = ?
                """;

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(UPDATE_RECIPE_QUERY)) {

            stmt.setString(1, recipe.getName());
            stmt.setString(2, recipe.getCategory());
            stmt.setString(3, recipe.getArea());
            stmt.setString(4, recipe.getInstructions());
            stmt.setString(5, recipe.getImage());
            stmt.setString(6, recipe.getIngredients());
            stmt.setDate(7, recipe.getPublishedOn());
            stmt.setInt(8, recipe.getRecipedBy());
            stmt.setString(9, recipe.getPrepareTime());
            stmt.setString(10, recipe.getCookingTime());
            stmt.setString(11, recipe.getYield());
            stmt.setString(12, recipe.getShortDescription());
            stmt.setString(13, recipe.getAccessType());
            stmt.setInt(14, recipe.getId());

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error updating recipe: " + e.getMessage(), e);
        }
    }

    @Override
    public void deleteRecipe(int id) {
        String DELETE_RECIPE_BY_ID_QUERY = "DELETE FROM recipes WHERE id = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(DELETE_RECIPE_BY_ID_QUERY)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting recipe: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean existsById(int id) {
        String EXIST_BY_ID_QUERY = "SELECT 1 FROM recipes WHERE id = ? LIMIT 1";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(EXIST_BY_ID_QUERY)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException("SQL Exception: " + e.getMessage(), e);
        }
    }

    @Override
    public Recipe getRecipeById(int id) {
        String SELECT_RECIPE_BY_ID_QUERY = "SELECT * FROM recipes WHERE id = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_RECIPE_BY_ID_QUERY)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToRecipe(rs);
                } else {
                    return null;
                }
            }

        } catch (SQLException ex) {
            throw new RuntimeException("SQL Exception: " + ex.getMessage(), ex);
        }
    }

    @Override
    public List<Recipe> getAllRecipes(int page, int size) {
        String SELECT_ALL_RECIPES_WITH_LIMIT_AND_OFFSET_QUERY = """
                SELECT * FROM recipes LIMIT ? OFFSET ?
                """;

        List<Recipe> recipes = new ArrayList<>();
        int offset = (page - 1) * size;

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_ALL_RECIPES_WITH_LIMIT_AND_OFFSET_QUERY)) {

            stmt.setInt(1, size);
            stmt.setInt(2, offset);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    recipes.add(mapResultSetToRecipe(rs));
                }
                return recipes;
            }
        } catch (SQLException ex) {
            throw new RuntimeException("SQL Exception: " + ex.getMessage(), ex);
        }
    }

    @Override
    public List<Recipe> searchAndFilterRecipes(String keyword, String category, String area, int recipedBy, int page, int size) {
        List<Recipe> recipes = new ArrayList<>();
        int offset = (page - 1) * size;

        StringBuilder sql = new StringBuilder("SELECT * FROM recipes");
        List<Object> params = new ArrayList<>();

        boolean hasCondition = false;

        if (keyword != null && !keyword.isEmpty()) {
            sql.append(hasCondition ? " AND" : " WHERE");
            sql.append(" name LIKE ?");
            params.add("%" + keyword + "%");
            hasCondition = true;
        }

        if (category != null && !category.isEmpty()) {
            sql.append(hasCondition ? " AND" : " WHERE");
            sql.append(" category = ?");
            params.add(category);
            hasCondition = true;
        }

        if (area != null && !area.isEmpty()) {
            sql.append(hasCondition ? " AND" : " WHERE");
            sql.append(" area = ?");
            params.add(area);
            hasCondition = true;
        }

        if (recipedBy > 0) {
            sql.append(hasCondition ? " AND" : " WHERE");
            sql.append(" reciped_by = ?");
            params.add(recipedBy);
            hasCondition = true;
        }

        sql.append(" LIMIT ? OFFSET ?");
        params.add(size);
        params.add(offset);

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    recipes.add(mapResultSetToRecipe(rs));
                }
                return recipes;
            }
        } catch (SQLException ex) {
            throw new RuntimeException("SQL Exception: " + ex.getMessage(), ex);
        }
    }

    @Override
    public int countSearchAndFilterRecipes(String keyword, String category, String area, int recipedBy) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM recipes");
        List<Object> params = new ArrayList<>();

        boolean hasCondition = false;

        if (keyword != null && !keyword.isEmpty()) {
            sql.append(hasCondition ? " AND" : " WHERE");
            sql.append(" name LIKE ?");
            params.add("%" + keyword + "%");
            hasCondition = true;
        }

        if (category != null && !category.isEmpty()) {
            sql.append(hasCondition ? " AND" : " WHERE");
            sql.append(" category = ?");
            params.add(category);
            hasCondition = true;
        }

        if (area != null && !area.isEmpty()) {
            sql.append(hasCondition ? " AND" : " WHERE");
            sql.append(" area = ?");
            params.add(area);
            hasCondition = true;
        }

        if (recipedBy > 0) {
            sql.append(hasCondition ? " AND" : " WHERE");
            sql.append(" reciped_by = ?");
            params.add(recipedBy);
            hasCondition = true;
        }

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
                return 0;
            }
        } catch (SQLException ex) {
            throw new RuntimeException("SQL Exception: " + ex.getMessage(), ex);
        }
    }

    @Override
    public int countAllRecipes() {
        String COUNT_ALL_RECIPES_QUERY = "SELECT COUNT(*) FROM recipes";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(COUNT_ALL_RECIPES_QUERY);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            }
            return 0;
        } catch (SQLException ex) {
            throw new RuntimeException("SQL Exception: " + ex.getMessage(), ex);
        }
    }

    @Override
    public int countAllRecipesByUserId(int userId) {
        String COUNT_ALL_RECIPES_BY_USER_ID_QUERY = "SELECT COUNT(*) FROM recipes WHERE reciped_by = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(COUNT_ALL_RECIPES_BY_USER_ID_QUERY)) {

            stmt.setInt(1, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
                return 0;
            }
        } catch (SQLException ex) {
            throw new RuntimeException("SQL Exception: " + ex.getMessage(), ex);
        }
    }

    @Override
    public int countAllFreeRecipesByCategory(String category) {
        String COUNT_ALL_FREE_RECIPES_BY_CATEGORY_ID_QUERY = """
                SELECT COUNT(*) FROM recipes WHERE category = ? AND access_type = 'FREE'
                """;

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(COUNT_ALL_FREE_RECIPES_BY_CATEGORY_ID_QUERY)) {

            stmt.setString(1, category);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
                return 0;
            }
        } catch (SQLException ex) {
            throw new RuntimeException("SQL Exception: " + ex.getMessage(), ex);
        }
    }

    @Override
    public int countAllRecipesByCategory(String category) {
        String COUNT_ALL_RECIPES_BY_CATEGORY_ID_QUERY = """
                SELECT COUNT(*) FROM recipes WHERE category = ?
                """;

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(COUNT_ALL_RECIPES_BY_CATEGORY_ID_QUERY)) {

            stmt.setString(1, category);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
                return 0;
            }
        } catch (SQLException ex) {
            throw new RuntimeException("SQL Exception: " + ex.getMessage(), ex);
        }
    }

    @Override
    public int countAllFreeRecipesByArea(String area) {
        String COUNT_ALL_FREE_RECIPES_BY_AREA_ID_QUERY = """
                SELECT COUNT(*) FROM recipes WHERE area = ? AND access_type = 'FREE'
                """;

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(COUNT_ALL_FREE_RECIPES_BY_AREA_ID_QUERY)) {

            stmt.setString(1, area);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
                return 0;
            }
        } catch (SQLException ex) {
            throw new RuntimeException("SQL Exception: " + ex.getMessage(), ex);
        }
    }

    @Override
    public int countAllRecipesByArea(String area) {
        String COUNT_ALL_RECIPES_BY_AREA_ID_QUERY = """
                SELECT COUNT(*) FROM recipes WHERE area = ?
                """;

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(COUNT_ALL_RECIPES_BY_AREA_ID_QUERY)) {

            stmt.setString(1, area);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
                return 0;
            }
        } catch (SQLException ex) {
            throw new RuntimeException("SQL Exception: " + ex.getMessage(), ex);
        }
    }

    @Override
    public List<Recipe> getAllRecipesByUserId(int userId, int page, int size) {
        String SELECT_ALL_RECIPES_BY_USER_ID_WITH_LIMIT_AND_OFFSET_QUERY = """
                SELECT * FROM recipes WHERE reciped_by = ? LIMIT ? OFFSET ?
                """;

        List<Recipe> recipes = new ArrayList<>();
        int offset = (page - 1) * size;

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_ALL_RECIPES_BY_USER_ID_WITH_LIMIT_AND_OFFSET_QUERY)) {

            stmt.setInt(1, userId);
            stmt.setInt(2, size);
            stmt.setInt(3, offset);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    recipes.add(mapResultSetToRecipe(rs));
                }
                return recipes;
            }
        } catch (SQLException ex) {
            throw new RuntimeException("SQL Exception: " + ex.getMessage(), ex);
        }
    }

    @Override
    public Recipe getFreeRecipeById(int id) {

        String SELECT_RECIPE_BY_ID_QUERY = "SELECT * FROM recipes WHERE id = ? AND access_type = 'FREE'";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_RECIPE_BY_ID_QUERY)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToRecipe(rs);
                } else {
                    return null;
                }
            }

        } catch (SQLException ex) {
            throw new RuntimeException("SQL Exception: " + ex.getMessage(), ex);
        }

    }

    @Override
    public List<Recipe> getAllFreeRecipes(int page, int size) {

        String SELECT_ALL_RECIPES_WITH_LIMIT_AND_OFFSET_QUERY = """
                SELECT * FROM recipes WHERE access_type = 'FREE' LIMIT ? OFFSET ?
                """;

        List<Recipe> recipes = new ArrayList<>();
        int offset = (page - 1) * size;

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_ALL_RECIPES_WITH_LIMIT_AND_OFFSET_QUERY)) {

            stmt.setInt(1, size);
            stmt.setInt(2, offset);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    recipes.add(mapResultSetToRecipe(rs));
                }
                return recipes;
            }
        } catch (SQLException ex) {
            throw new RuntimeException("SQL Exception: " + ex.getMessage(), ex);
        }
    }

    @Override
    public List<Recipe> searchAndFilterFreeRecipes(String keyword, String category, String area, int recipedBy, String accessType, int page, int size) {
        List<Recipe> recipes = new ArrayList<>();
        int offset = (page - 1) * size;

        StringBuilder sql = new StringBuilder("SELECT * FROM recipes");
        List<Object> params = new ArrayList<>();

        boolean hasCondition = false;

        if (keyword != null && !keyword.isEmpty()) {
            sql.append(hasCondition ? " AND" : " WHERE");
            sql.append(" name LIKE ?");
            params.add("%" + keyword + "%");
            hasCondition = true;
        }

        if (category != null && !category.isEmpty()) {
            sql.append(hasCondition ? " AND" : " WHERE");
            sql.append(" category = ?");
            params.add(category);
            hasCondition = true;
        }

        if (area != null && !area.isEmpty()) {
            sql.append(hasCondition ? " AND" : " WHERE");
            sql.append(" area = ?");
            params.add(area);
            hasCondition = true;
        }

        if (recipedBy > 0) {
            sql.append(hasCondition ? " AND" : " WHERE");
            sql.append(" reciped_by = ?");
            params.add(recipedBy);
            hasCondition = true;
        }

        if (accessType != null && !accessType.isEmpty()) {
            sql.append(hasCondition ? " AND" : " WHERE");
            sql.append(" access_type = ?");
            params.add(accessType);
            hasCondition = true;
        }

        sql.append(" LIMIT ? OFFSET ?");
        params.add(size);
        params.add(offset);

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    recipes.add(mapResultSetToRecipe(rs));
                }
                return recipes;
            }
        } catch (SQLException ex) {
            throw new RuntimeException("SQL Exception: " + ex.getMessage(), ex);
        }
    }

    @Override
    public int countSearchAndFilterFreeRecipes(String keyword, String category, String area, int recipedBy, String accessType) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM recipes");
        List<Object> params = new ArrayList<>();

        boolean hasCondition = false;

        if (keyword != null && !keyword.isEmpty()) {
            sql.append(hasCondition ? " AND" : " WHERE");
            sql.append(" name LIKE ?");
            params.add("%" + keyword + "%");
            hasCondition = true;
        }

        if (category != null && !category.isEmpty()) {
            sql.append(hasCondition ? " AND" : " WHERE");
            sql.append(" category = ?");
            params.add(category);
            hasCondition = true;
        }

        if (area != null && !area.isEmpty()) {
            sql.append(hasCondition ? " AND" : " WHERE");
            sql.append(" area = ?");
            params.add(area);
            hasCondition = true;
        }

        if (recipedBy > 0) {
            sql.append(hasCondition ? " AND" : " WHERE");
            sql.append(" reciped_by = ?");
            params.add(recipedBy);
            hasCondition = true;
        }

        if (accessType != null && !accessType.isEmpty()) {
            sql.append(hasCondition ? " AND" : " WHERE");
            sql.append(" access_type = ?");
            params.add(accessType);
            hasCondition = true;
        }

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
                return 0;
            }
        } catch (SQLException ex) {
            throw new RuntimeException("SQL Exception: " + ex.getMessage(), ex);
        }
    }

    @Override
    public int countAllFreeRecipes() {

        String COUNT_ALL_RECIPES_QUERY = """
                SELECT COUNT(*) FROM recipes WHERE access_type = 'FREE'
                """;

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(COUNT_ALL_RECIPES_QUERY);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            }
            return 0;
        } catch (SQLException ex) {
            throw new RuntimeException("SQL Exception: " + ex.getMessage(), ex);
        }

    }

    @Override
    public List<Recipe> getAllFreeRecipesByCategory(String category, int page, int size) {
        String SELECT_ALL_RECIPES_BY_CATEGORY_ID_WITH_LIMIT_AND_OFFSET_QUERY = """
                SELECT * FROM recipes WHERE category = ? AND access_type = 'FREE' LIMIT ? OFFSET ?
                """;

        List<Recipe> recipes = new ArrayList<>();
        int offset = (page - 1) * size;

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_ALL_RECIPES_BY_CATEGORY_ID_WITH_LIMIT_AND_OFFSET_QUERY)) {

            stmt.setString(1, category);
            stmt.setInt(2, size);
            stmt.setInt(3, offset);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    recipes.add(mapResultSetToRecipe(rs));
                }
                return recipes;
            }
        } catch (SQLException ex) {
            throw new RuntimeException("SQL Exception: " + ex.getMessage(), ex);
        }
    }

    @Override
    public List<Recipe> getAllRecipesByCategory(String category, int page, int size) {
        String SELECT_ALL_RECIPES_BY_CATEGORY_ID_WITH_LIMIT_AND_OFFSET_QUERY = """
                SELECT * FROM recipes WHERE category = ? LIMIT ? OFFSET ?
                """;

        List<Recipe> recipes = new ArrayList<>();
        int offset = (page - 1) * size;

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_ALL_RECIPES_BY_CATEGORY_ID_WITH_LIMIT_AND_OFFSET_QUERY)) {

            stmt.setString(1, category);
            stmt.setInt(2, size);
            stmt.setInt(3, offset);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    recipes.add(mapResultSetToRecipe(rs));
                }
                return recipes;
            }
        } catch (SQLException ex) {
            throw new RuntimeException("SQL Exception: " + ex.getMessage(), ex);
        }
    }

    @Override
    public List<Recipe> getAllFreeRecipesByArea(String area, int page, int size) {
        String SELECT_ALL_RECIPES_BY_AREA_ID_WITH_LIMIT_AND_OFFSET_QUERY = """
                SELECT * FROM recipes WHERE area = ? AND access_type = 'FREE' LIMIT ? OFFSET ?
                """;

        List<Recipe> recipes = new ArrayList<>();
        int offset = (page - 1) * size;

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_ALL_RECIPES_BY_AREA_ID_WITH_LIMIT_AND_OFFSET_QUERY)) {

            stmt.setString(1, area);
            stmt.setInt(2, size);
            stmt.setInt(3, offset);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    recipes.add(mapResultSetToRecipe(rs));
                }
                return recipes;
            }
        } catch (SQLException ex) {
            throw new RuntimeException("SQL Exception: " + ex.getMessage(), ex);
        }
    }

    @Override
    public List<Recipe> getAllRecipesByArea(String area, int page, int size) {
        String SELECT_ALL_RECIPES_BY_AREA_ID_WITH_LIMIT_AND_OFFSET_QUERY = """
                SELECT * FROM recipes WHERE area = ? LIMIT ? OFFSET ?
                """;

        List<Recipe> recipes = new ArrayList<>();
        int offset = (page - 1) * size;

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_ALL_RECIPES_BY_AREA_ID_WITH_LIMIT_AND_OFFSET_QUERY)) {

            stmt.setString(1, area);
            stmt.setInt(2, size);
            stmt.setInt(3, offset);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    recipes.add(mapResultSetToRecipe(rs));
                }
                return recipes;
            }
        } catch (SQLException ex) {
            throw new RuntimeException("SQL Exception: " + ex.getMessage(), ex);
        }
    }

    private Recipe mapResultSetToRecipe(ResultSet rs) throws SQLException {
        Recipe recipe = new Recipe();
        recipe.setId(rs.getInt("id"));
        recipe.setName(rs.getString("name"));
        recipe.setCategory(rs.getString("category"));
        recipe.setArea(rs.getString("area"));
        recipe.setInstructions(rs.getString("instructions"));
        recipe.setImage(rs.getString("image"));
        recipe.setIngredients(rs.getString("ingredients"));
        recipe.setPublishedOn(rs.getDate("published_on"));
        recipe.setRecipedBy(rs.getInt("reciped_by"));
        recipe.setPrepareTime(rs.getString("prepare_time"));
        recipe.setCookingTime(rs.getString("cooking_time"));
        recipe.setYield(rs.getString("yield"));
        recipe.setShortDescription(rs.getString("short_description"));
        recipe.setAccessType(rs.getString("access_type"));

        return recipe;
    }
}
