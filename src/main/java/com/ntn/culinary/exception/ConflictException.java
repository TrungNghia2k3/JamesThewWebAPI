package com.ntn.culinary.exception;

// Dữ liệu đã tồn tại/xung đột trạng thái
// Ví dụ:
// Đăng ký username đã tồn tại
// Tạo bài thi mà đã tồn tại (examId trùng)
// Examiner đã đánh giá bài thi này rồi mà lại thêm nữa.
public class ConflictException extends RuntimeException {
    public ConflictException(String message) {
        super(message);
    }
}
