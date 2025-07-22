package com.ntn.culinary.response;

public class LoginResponse {
    private String token;
    private String error;

    public LoginResponse() {
    }

    public LoginResponse(String token, String error) {
        this.token = token;
        this.error = error;
    }

    public String getToken() {return token;}

    public void setToken(String token) {this.token = token;}

    public String getError() {return error;}

    public void setError(String error) {this.error = error;}
}
