package com.ntn.culinary.service;

import com.ntn.culinary.request.LoginRequest;

public interface AuthService {
    /**
         * Authenticates a user with the provided username and password.
         *
         * @param loginRequest the user's username and password encapsulated in a request object
         * @return a token if authentication is successful, otherwise null or an error message
         */
        String authenticate(LoginRequest loginRequest);
}
