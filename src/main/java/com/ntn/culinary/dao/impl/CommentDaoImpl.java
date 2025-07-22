package com.ntn.culinary.dao.impl;

import com.ntn.culinary.dao.CommentDao;
import com.ntn.culinary.model.Comment;
import com.ntn.culinary.utils.DatabaseUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.ntn.culinary.utils.DatabaseUtils.getConnection;

public class CommentDaoImpl implements CommentDao {

    @Override
    public List<Comment> getCommentsByRecipeId(int recipeId) {

        String SELECT_COMMENTS_BY_RECIPE_ID_QUERY = """
                SELECT * FROM comments WHERE recipe_id = ?
                """;

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_COMMENTS_BY_RECIPE_ID_QUERY)) {

            stmt.setInt(1, recipeId);

            List<Comment> comments = new ArrayList<>();

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Comment comment = new Comment();
                    comment.setId(rs.getInt("id"));
                    comment.setUserId(rs.getInt("user_id"));
                    comment.setRecipeId(rs.getInt("recipe_id"));
                    comment.setContent(rs.getString("content"));
                    comment.setDate(rs.getTimestamp("date"));
                    comment.setRating(rs.getInt("rating"));
                    comment.setBanned(rs.getBoolean("is_banned"));
                    comments.add(comment);
                }
                return comments;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving comments: " + e.getMessage(), e);
        }

    }

    @Override
    public Comment getCommentById(int id) {
        String SELECT_COMMENT_BY_ID_QUERY = """
                SELECT * FROM comments WHERE id = ?
                """;

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_COMMENT_BY_ID_QUERY)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Comment comment = new Comment();
                    comment.setId(rs.getInt("id"));
                    comment.setUserId(rs.getInt("user_id"));
                    comment.setRecipeId(rs.getInt("recipe_id"));
                    comment.setContent(rs.getString("content"));
                    comment.setDate(rs.getTimestamp("date"));
                    comment.setRating(rs.getInt("rating"));
                    comment.setBanned(rs.getBoolean("is_banned"));
                    return comment;
                } else {
                    throw new RuntimeException("Comment not found with ID: " + id);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving comment by ID: " + e.getMessage(), e);
        }
    }

    @Override
    public void addComment(Comment comment) {
        String INSERT_COMMENT_QUERY = """
                INSERT INTO comments (user_id, recipe_id, content, date, rating)
                VALUES (?, ?, ?, ?, ?)
                """;

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(INSERT_COMMENT_QUERY)) {

            stmt.setInt(1, comment.getUserId());
            stmt.setInt(2, comment.getRecipeId());
            stmt.setString(3, comment.getContent());
            stmt.setTimestamp(4, comment.getDate());
            stmt.setInt(5, comment.getRating());

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error adding comment: " + e.getMessage(), e);
        }
    }

    @Override
    public void deleteCommentById(int id) {
        String DELETE_COMMENT_BY_ID_QUERY = """
                DELETE FROM comments WHERE id = ?
                """;

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(DELETE_COMMENT_BY_ID_QUERY)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting comment: " + e.getMessage(), e);
        }
    }

    @Override
    public void updateComment(Comment comment) {
        String UPDATE_COMMENT_QUERY = """
                UPDATE comments SET user_id = ?, recipe_id = ?, content = ?, date = ?, rating = ?
                WHERE id = ?
                """;

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(UPDATE_COMMENT_QUERY)) {

            stmt.setInt(1, comment.getUserId());
            stmt.setInt(2, comment.getRecipeId());
            stmt.setString(3, comment.getContent());
            stmt.setTimestamp(4, comment.getDate());
            stmt.setInt(5, comment.getRating());
            stmt.setInt(6, comment.getId());

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error updating comment: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean existsById(int id) {
        String EXISTS_COMMENT_BY_ID_QUERY = """
                SELECT 1 FROM comments WHERE id = ? LIMIT 1
                """;

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(EXISTS_COMMENT_BY_ID_QUERY)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error checking if comment exists: " + e.getMessage(), e);
        }
        return false;
    }

    @Override
    public void banCommentById(int id) {
        String BAN_COMMENT_BY_ID_QUERY = """
                UPDATE comments SET isBanned = TRUE WHERE id = ?
                """;

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(BAN_COMMENT_BY_ID_QUERY)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error banning comment: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Comment> getAllCommentsByUserId(int userId) {
        String SELECT_COMMENTS_BY_USER_ID_QUERY = """
                SELECT * FROM comments WHERE user_id = ?
                """;

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_COMMENTS_BY_USER_ID_QUERY)) {

            stmt.setInt(1, userId);

            List<Comment> comments = new ArrayList<>();

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Comment comment = new Comment();
                    comment.setId(rs.getInt("id"));
                    comment.setUserId(rs.getInt("user_id"));
                    comment.setRecipeId(rs.getInt("recipe_id"));
                    comment.setContent(rs.getString("content"));
                    comment.setDate(rs.getTimestamp("date"));
                    comment.setRating(rs.getInt("rating"));
                    comments.add(comment);
                }
                return comments;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving comments by user ID: " + e.getMessage(), e);
        }
    }

    @Override
    public Map<Integer, List<Comment>> getCommentsByRecipeIds(List<Integer> recipeIds) {
        Map<Integer, List<Comment>> result = new HashMap<>();

        if (recipeIds == null || recipeIds.isEmpty()) return result;

        StringBuilder query = new StringBuilder("SELECT * FROM comments WHERE recipe_id IN (");
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
                Comment comment = new Comment();
                comment.setId(rs.getInt("id"));
                comment.setRecipeId(rs.getInt("recipe_id"));
                comment.setContent(rs.getString("content"));
                comment.setDate(rs.getTimestamp("date"));

                result.computeIfAbsent(comment.getRecipeId(), k -> new ArrayList<>()).add(comment);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error fetching comments by recipeIds", e);
        }

        return result;
    }

}
