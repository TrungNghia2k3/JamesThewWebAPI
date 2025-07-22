package com.ntn.culinary.servlet.staff;

import com.ntn.culinary.dao.CommentDao;
import com.ntn.culinary.dao.RecipeDao;
import com.ntn.culinary.dao.UserDao;
import com.ntn.culinary.dao.impl.CommentDaoImpl;
import com.ntn.culinary.dao.impl.RecipeDaoImpl;
import com.ntn.culinary.dao.impl.UserDaoImpl;
import com.ntn.culinary.exception.NotFoundException;
import com.ntn.culinary.response.ApiResponse;
import com.ntn.culinary.service.CommentService;
import com.ntn.culinary.service.impl.CommentServiceImpl;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static com.ntn.culinary.response.ApiResponse.error;
import static com.ntn.culinary.response.ApiResponse.success;
import static com.ntn.culinary.utils.ResponseUtils.sendResponse;

@WebServlet("/api/protected/staff/comments")
public class CommentServlet extends HttpServlet {
    private final CommentService commentService;

    public CommentServlet() {
        UserDao userDao = new UserDaoImpl();
        RecipeDao recipeDao = new RecipeDaoImpl();
        CommentDao commentDao = new CommentDaoImpl();
        this.commentService = new CommentServiceImpl(userDao, recipeDao, commentDao);
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) {
        try {
            int commentId = Integer.parseInt(req.getParameter("id"));
            commentService.banComment(commentId);
            sendResponse(resp, success(200, "Comment banned successfully"));
        } catch (NumberFormatException e) {
            sendResponse(resp, error(400, "Invalid comment ID format"));
        } catch (NotFoundException e) {
            sendResponse(resp, error(404, e.getMessage()));
        } catch (Exception e) {
            sendResponse(resp, error(500, "Internal server error: " + e.getMessage()));
        }
    }
}
