package com.ntn.culinary.service.impl;

import com.ntn.culinary.dao.CommentDao;
import com.ntn.culinary.dao.RecipeDao;
import com.ntn.culinary.dao.UserDao;
import com.ntn.culinary.exception.NotFoundException;
import com.ntn.culinary.model.Comment;
import com.ntn.culinary.request.CommentRequest;
import com.ntn.culinary.response.CommentResponse;
import com.ntn.culinary.service.CommentService;

import java.sql.Timestamp;
import java.util.List;

public class CommentServiceImpl implements CommentService {
    private final UserDao userDao;
    private final RecipeDao recipeDao;
    private final CommentDao commentDao;

    public CommentServiceImpl(UserDao userDao, RecipeDao recipeDao, CommentDao commentDao) {
        this.userDao = userDao;
        this.recipeDao = recipeDao;
        this.commentDao = commentDao;
    }

    @Override
    public List<CommentResponse> getCommentsByUserId(int userId) {
        if (!userDao.existsById(userId)) {
            throw new NotFoundException("User does not exist");
        }
        return commentDao.getAllCommentsByUserId(userId).stream()
                .map(this::mapCommentToResponse)
                .toList();
    }

    @Override
    public CommentResponse getCommentById(int id) {
        if (!commentDao.existsById(id)) {
            throw new NotFoundException("Comment does not exist");
        }
        Comment comment = commentDao.getCommentById(id);
        return mapCommentToResponse(comment);
    }

    @Override
    public List<CommentResponse> getCommentsByRecipeId(int recipeId) {
        if (!recipeDao.existsById(recipeId)) {
            throw new NotFoundException("Recipe does not exist");
        }
        return commentDao.getCommentsByRecipeId(recipeId).stream()
                .map(this::mapCommentToResponse)
                .toList();
    }

    @Override
    public void addComment(CommentRequest commentRequest) {
        validateCommentRequest(commentRequest);
        commentDao.addComment(mapRequestToComment(commentRequest));
    }

    @Override
    public void updateComment(CommentRequest commentRequest) {
        if (!commentDao.existsById(commentRequest.getId())) {
            throw new NotFoundException("Comment does not exist");
        }
        validateCommentRequest(commentRequest);
        commentDao.updateComment(mapRequestToComment(commentRequest));
    }

    @Override
    public void banComment(int id) {
        if (!commentDao.existsById(id)) {
            throw new NotFoundException("Comment does not exist");
        }
        commentDao.banCommentById(id);
    }

    @Override
    public void deleteComment(int id) {
        if (!commentDao.existsById(id)) {
            throw new NotFoundException("Comment does not exist");
        }
        commentDao.deleteCommentById(id);
    }

    private void validateCommentRequest(CommentRequest commentRequest) {
        if (!userDao.existsById(commentRequest.getUserId())) {
            throw new NotFoundException("User does not exist");
        }
        if (!recipeDao.existsById(commentRequest.getRecipeId())) {
            throw new NotFoundException("Recipe does not exist");
        }

        if (commentRequest.getRating() < 1 || commentRequest.getRating() > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }
    }

    private Comment mapRequestToComment(CommentRequest request) {
        return new Comment(
                request.getId(),
                request.getUserId(),
                request.getRecipeId(),
                request.getContent(),
                (Timestamp) request.getDate(),
                request.getRating()
        );
    }

    private CommentResponse mapCommentToResponse(Comment comment) {
        return new CommentResponse(
                comment.getId(),
                comment.getUserId(),
                comment.getRecipeId(),
                comment.getContent(),
                comment.getDate(),
                comment.getRating(),
                comment.isBanned()
        );
    }
}
