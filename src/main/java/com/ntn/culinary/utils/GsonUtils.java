package com.ntn.culinary.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class GsonUtils {

    private static final Gson gson = new GsonBuilder()
            .setDateFormat("yyyy-MM-dd")
            .setPrettyPrinting()
            .create();

    // Private constructor to prevent instantiation
    private GsonUtils() {
        // This class should not be instantiated
    }

    // Hàm lấy Gson
    public static Gson getGson() {
        return gson;
    }

    // Nếu muốn tiện hơn: hàm parse luôn
    public static <T> T fromJson(String json, Class<T> clazz) {
        return gson.fromJson(json, clazz);
    }

    // Hàm convert object -> JSON String
    public static String toJson(Object object) {
        return gson.toJson(object);
    }
}

