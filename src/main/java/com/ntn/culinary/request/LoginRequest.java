package com.ntn.culinary.request;

import com.google.gson.annotations.Expose;

public class LoginRequest {

    @Expose
    private String username;

    @Expose
    private String password;

    public LoginRequest() {}

    public LoginRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {this.username = username;}

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
