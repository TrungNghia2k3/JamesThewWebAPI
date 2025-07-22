package com.ntn.culinary.service;

import com.ntn.culinary.request.CommentRequest;
import com.ntn.culinary.response.CommentResponse;

import java.util.List;

public interface CommentService {
    List<CommentResponse> getCommentsByUserId(int userId);

    CommentResponse getCommentById(int id);

    List<CommentResponse> getCommentsByRecipeId(int recipeId);

    void addComment(CommentRequest commentRequest);

    void updateComment(CommentRequest commentRequest);

    void banComment(int id);

    void deleteComment(int id);
}
