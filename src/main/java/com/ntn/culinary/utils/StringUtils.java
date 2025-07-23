package com.ntn.culinary.utils;

public class StringUtils {
    public static String capitalize(String inputString) {

        // get the first character of the inputString
        char firstLetter = inputString.charAt(0);

        // convert it to an UpperCase letter
        char capitalFirstLetter = Character.toUpperCase(firstLetter);

        // return the output string by updating
        //the first char of the input string
        return inputString.replace(inputString.charAt(0), capitalFirstLetter);
    }

    /**
     * Hàm slugify đơn giản chuyển chuỗi thành dạng url-safe
     */
    public static String slugify(String input) {
        if (input == null) return "";
        return input.toLowerCase()
                .replaceAll("[^\\w\\s-]", "")
                .replaceAll("\\s+", "-")
                .trim();
    }
}
