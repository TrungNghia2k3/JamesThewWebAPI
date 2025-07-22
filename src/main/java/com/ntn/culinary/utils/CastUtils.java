package com.ntn.culinary.utils;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class CastUtils {
    /**
     * Chuyển object thành List<String> an toàn.
     * @param obj Object cần chuyển
     * @return List<String> (hoặc empty list nếu không hợp lệ)
     */
    public static List<String> toStringList(Object obj) {
        if (obj instanceof List<?>) {
            return ((List<?>) obj).stream()
                    .filter(Objects::nonNull)
                    .map(Object::toString)
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    /**
     * Chuyển object thành List<T> (có type), nếu có thể.
     * @param obj Object cần chuyển
     * @param clazz Kiểu class của T
     * @param <T> Kiểu phần tử
     * @return List<T> (hoặc empty list nếu không hợp lệ)
     */
    public static <T> List<T> toTypedList(Object obj, Class<T> clazz) {
        if (obj instanceof List<?> list) {
            return list.stream()
                    .filter(Objects::nonNull)
                    .filter(clazz::isInstance)
                    .map(clazz::cast)
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }
}
