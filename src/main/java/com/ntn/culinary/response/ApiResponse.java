package com.ntn.culinary.response;

import com.google.gson.annotations.Expose;

import java.util.Map;

public class ApiResponse<T> {

    @Expose
    private boolean success;

    @Expose
    private int status;

    @Expose
    private String message;

    @Expose
    private T data;

    @Expose
    private String error;

    // Private constructor để bắt buộc sử dụng factory methods
    private ApiResponse(boolean success, int status, String message, T data, String error) {
        this.success = success;
        this.status = status;
        this.message = message;
        this.data = data;
        this.error = error;
    }

    // Factory method: Success response with data
    public static <T> ApiResponse<T> success(int status, String message, T data) {
        return new ApiResponse<>(true, status, message, data, null);
    }

    // Factory method: Success response without data
    public static <T> ApiResponse<T> success(int status, String message) {
        return new ApiResponse<>(true, status, message, null, null);
    }

    // Factory method: Error response
    public static <T> ApiResponse<T> error(int status, String error) {
        return new ApiResponse<>(false, status, null, null, error);
    }

    // Factory method: Validation error response
    public static <T> ApiResponse<T> validationError(int status, String message, Map<String, String> errors) {
        // Ép kiểu Map thành T nếu T được dùng là Map<String, String>
        @SuppressWarnings("unchecked")
        T errorData = (T) errors;
        return new ApiResponse<>(false, status, message, errorData, null);
    }

    // Getters and Setters
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
