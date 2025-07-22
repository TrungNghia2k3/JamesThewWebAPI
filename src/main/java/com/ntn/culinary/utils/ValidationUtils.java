package com.ntn.culinary.utils;

import com.google.gson.JsonObject;

public class ValidationUtils {

    public static boolean isNotExistId(int id) {
        return id <= 0;
    }

    // Check if string is null or empty after trimming
    public static boolean isNullOrEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

    public static boolean isEmpty(String str) {
        return str.trim().isEmpty();
    }

    // Check if object is null
    public static boolean isNull(Object obj) {
        return obj == null;
    }

    // Example: check length constraints
    public static boolean isLengthBetween(String str, int min, int max) {
        return str != null && str.length() >= min && str.length() <= max;
    }

    // Example: simple email format check (you can improve this)
    public static boolean isValidEmail(String email) {
        return email != null && email.matches("^[\\w.-]+@[\\w.-]+\\.\\w+$");
    }

    // Example: simple phone number format
    public static boolean isValidPhone(String phone) {
        return phone != null && phone.matches("^\\+?[0-9]{7,15}$");
    }

    // Check if a field exists in the JSON and is not empty
    public static boolean isMissingOrEmpty(JsonObject json, String fieldName) {
        return !json.has(fieldName) || isNullOrEmpty(json.get(fieldName).getAsString());
    }

    // Validate multiple required fields
    public static boolean hasMissingFields(JsonObject json, String... requiredFields) {
        for (String field : requiredFields) {
            if (isMissingOrEmpty(json, field)) {
                return true;
            }
        }
        return false;
    }
}
