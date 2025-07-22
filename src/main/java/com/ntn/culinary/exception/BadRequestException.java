package com.ntn.culinary.exception;

// Client gửi request sai định dạng, sai tham số, dữ liệu không hợp lệ
// Ví dụ:
// Truy vấn: GET /api/users?page=-1
// Gửi JSON thiếu trường bắt buộc (username = null)
// Gửi dữ liệu sai định dạng (chuỗi thay vì số)
// Truy vấn filter không tồn tại (sortBy=unknownField)

public class BadRequestException extends RuntimeException {
    public BadRequestException(String message) {
        super(message);
    }
}
