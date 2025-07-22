package com.ntn.culinary.service;

import com.ntn.culinary.request.RegisterRequest;
import com.ntn.culinary.request.UserRequest;
import com.ntn.culinary.response.UserResponse;

import java.util.List;

public interface UserService {
    List<UserResponse> getAllUsers();

    UserResponse getUserById(int id);

    void register(RegisterRequest request);

    void updateGeneralUser(UserRequest request);

    void deleteUser(int id);

    void toggleUserStatus(int id);

    boolean isSubscriptionValid(int id);
}
