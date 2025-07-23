package com.ntn.culinary.utils;

import javax.servlet.http.Part;
import java.io.File;
import java.io.IOException;

public class ImageUtils {

    /**
     * Lưu file ảnh upload vào thư mục /images/recipes của webapp
     *
     * @param imagePart Part lấy từ request.getPart("image")
     * @param servletContext để lấy đường dẫn tuyệt đối
     * @param baseFileName Chuỗi base để tạo tên file (vd: slug của tên recipe)
     * @return tên file đã lưu (vd: apple-frangipan-1689012345678.jpg)
     * @throws IOException
     */
    // Chỉ định đường dẫn tuyệt đối đến thư mục chứa ảnh
    private static final String IMAGE_DIRECTORY = "E:/Project/JamesThewWebApplication/source-code/backend/images";

    public static String saveImage(Part imagePart, String baseFileName, String type) {
        if (imagePart == null || imagePart.getSize() == 0) {
            return null;
        }

        // Tạo thư mục nếu chưa tồn tại
        File uploadDir = new File(IMAGE_DIRECTORY + "/" + type);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        // Lấy phần mở rộng của ảnh
        String submittedFileName = imagePart.getSubmittedFileName();
        String ext = "";
        int i = submittedFileName.lastIndexOf('.');
        if (i > 0) {
            ext = submittedFileName.substring(i);
        }

        // Tạo tên file mới
        String filename = baseFileName + "-" + System.currentTimeMillis() + ext;
        File file = new File(uploadDir, filename);

        // Ghi file
        try {
            imagePart.write(file.getAbsolutePath());

        } catch (IOException ex) {
            throw new RuntimeException("Could not save image", ex);
        }

        return filename;
    }

    /**
     * Xóa ảnh đã lưu từ thư mục chứa ảnh
     *
     * @param filename Tên file cần xóa (vd: apple-frangipan-1689012345678.jpg)
     * @param type     Thư mục con (vd: "recipes", "avatars", ...)
     */
    public static void deleteImage(String filename, String type) {
        if (filename == null || filename.isEmpty()) {
            return;
        }

        File file = new File(IMAGE_DIRECTORY + "/" + type, filename);
        if (file.exists()) {
            file.delete();
        }
    }
}
