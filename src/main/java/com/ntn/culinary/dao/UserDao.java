package com.ntn.culinary.dao;

import com.ntn.culinary.model.User;

import java.util.List;

public interface UserDao {
    void updateUser(User user, String avatarPath);

    boolean existsById(int id);

    List<User> getAllUsers();

    User getUserById(int id);

    void addUser(User user);

    void deleteUser(int id);

    void toggleUserActiveStatus(int id);

    User findUserByUsername(String username);

    boolean isSubscriptionValid(int userId);

    boolean userExists(int id);

    List<User> getUsersByRole(String role);

    List<User> getUsersByPermission(String permission);

    List<User> getUsersByUsername(String username);

    List<User> getUsersByEmail(String email);
}
